//
//  TreasureDetailViewController.m
//  trsly
//
//  Created by Niek Willems on 18/03/14.
//  Copyright (c) 2014 Niek Willems. All rights reserved.
//

#import "TreasureDetailViewController.h"

@interface TreasureDetailViewController ()

@property (nonatomic, strong) NSMutableData *buffer;
@property (nonatomic, strong) NSURLConnection *connection;

@property (strong, nonatomic) IBOutlet UITableView *commentView;

@end

@implementation TreasureDetailViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.commentView.dataSource = self;
	// Do any additional setup after loading the view.
    self.title = [self.treasureDetail objectForKey:@"title"];
    self.treasureTitle.text = [self.treasureDetail objectForKey:@"text"];
    
    // Set the image in imageView
    NSString *base = [[NSUserDefaults standardUserDefaults] stringForKey:@"baseUrl"];
    NSString *imageUrl = [NSString stringWithFormat:@"%@public%@", base, [self.treasureDetail objectForKey:@"media"]];
    self.treasureImage.image = [UIImage imageWithData:[NSData dataWithContentsOfURL:[NSURL URLWithString:imageUrl]]];
    
    self.commentsArray = [[NSArray alloc] init];
    [self sendRequest:nil];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    // Return the number of sections.
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    // Return the number of rows in the section.
    return [self.commentsArray count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"Cell";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier forIndexPath:indexPath];
    
    NSDictionary *tempDictionary = [self.commentsArray objectAtIndex:indexPath.row];
    
    cell.textLabel.text= [tempDictionary objectForKey:@"text"];
    UIFont *font = [UIFont fontWithName:@"Arial" size:12.0];
    cell.textLabel.font = font;
    
    cell.accessoryType = NO;
    
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 4.0;
}

- (IBAction)sendRequest:(id)sender {
    // Disable the refresh button during request
    [self.refreshButton setEnabled:NO];
    // Create the request
    NSString *base = [[NSUserDefaults standardUserDefaults] stringForKey:@"baseUrl"];
    NSString *treasureId = [self.treasureDetail objectForKey:@"_id"];
    NSString *url = [NSString stringWithFormat:@"%@%s%@", base, "comments/", treasureId];
    NSURLRequest *request = [NSURLRequest requestWithURL:[NSURL URLWithString:url]];
    // Create connection
    self.connection = [NSURLConnection connectionWithRequest:request delegate:self];
    // Ensure connection was created
    if (self.connection) {
        // Initialize buffer
        self.buffer = [NSMutableData data];
        // Start request
        [self.connection start];
    } else {
        // Connection failed
    }
}

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error
{
    // Clear connection and buffer
    self.connection = nil;
    self.buffer = nil;
    // Re-enable refresh button
    [self.refreshButton setEnabled:YES];
    
    NSLog(@"Connection failed! Error - %@ %@",
          [error localizedDescription],
          [[error userInfo] objectForKey:NSURLErrorFailingURLStringErrorKey]);
}

- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response
{
    // Reset buffer length
    [self.buffer setLength:0];
}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data
{
    // Append data to buffer
    [self.buffer appendData:data];
}

- (void)connectionDidFinishLoading:(NSURLConnection *)connection
{
    // Dispatch from main queue for json processing
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        NSError *error = nil;
        self.commentsArray = [NSJSONSerialization JSONObjectWithData:_buffer options:kNilOptions error:&error];
        
        // Back to main queu
        dispatch_async(dispatch_get_main_queue(), ^{
            // Check for any error
            if (!error) {
                [self.commentView reloadData];
                //self.nearbyTreasuresArray = [json objectForKey:@"title"];
            } else {
                // Request failed
            }
            
            // Re-enable refresh button
            [self.refreshButton setEnabled:YES];
            // Clear connection and buffer
            self.connection = nil;
            self.buffer = nil;
        });
    });
}

@end
