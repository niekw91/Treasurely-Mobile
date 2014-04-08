//
//  TreasureViewController.m
//  trsly
//
//  Created by Niek Willems on 18/03/14.
//  Copyright (c) 2014 Niek Willems. All rights reserved.
//

#import "TreasureViewController.h"
#import "TreasureDetailViewController.h"
#import "LoginViewController.h"
#import <CoreLocation/CoreLocation.h>

@interface TreasureViewController () <CLLocationManagerDelegate>

@property (nonatomic, strong) NSMutableData *buffer;
@property (nonatomic, strong) NSURLConnection *connection;

@property (nonatomic, strong) NSString *latitude;
@property (nonatomic, strong) NSString *longitude;

@property (strong, nonatomic) IBOutlet UITableView *tableView;

@end

@implementation TreasureViewController {
    
    CLLocationManager *manager;
    CLGeocoder *geocoder;
    CLPlacemark *placemark;
    
}

- (id)initWithStyle:(UITableViewStyle)style
{
    self = [super initWithStyle:style];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidAppear:(BOOL)animated
{
    // Set UIToolbar visible
    [self.navigationController setToolbarHidden:NO];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    manager = [[CLLocationManager alloc] init];
    geocoder = [[CLGeocoder alloc] init];
    self.nearbyTreasuresArray = [[NSArray alloc] init];
    //[self sendRequest:nil];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)logoutAction:(id)sender {
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [defaults removeObjectForKey:@"token"];
    [defaults synchronize];
    // Perform segue
    [self performSegueWithIdentifier:@"logout" sender:self];
}

#pragma mark - Prepare for seque

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    if ([self.tableView indexPathForSelectedRow]) {
        NSIndexPath *indexPath = [self.tableView indexPathForSelectedRow];
        TreasureDetailViewController *detailViewController = (TreasureDetailViewController *)segue.destinationViewController;
        detailViewController.treasureDetail = [self.nearbyTreasuresArray objectAtIndex:indexPath.row];
    }
    if ([[segue identifier] isEqualToString:@"logout"]) {
        LoginViewController *loginViewController = (LoginViewController *)segue.destinationViewController;
    }

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
    return [self.nearbyTreasuresArray count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"Cell";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier forIndexPath:indexPath];
    
    NSDictionary *tempDictionary = [self.nearbyTreasuresArray objectAtIndex:indexPath.row];
    
    cell.textLabel.text= [tempDictionary objectForKey:@"title"];
    cell.detailTextLabel.text = [tempDictionary objectForKey:@"text"];
    
    cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 60.0;
}

- (void)getCurrentLocation
{
    manager.delegate = self;
    manager.desiredAccuracy = kCLLocationAccuracyBest;
    
    [manager startUpdatingLocation];
}

#pragma mark CLLocationManagerDelegate Methods

- (void)locationManager:(CLLocationManager *)manager didFailWithError:(NSError *)error
{
    // Failed to get location
    NSLog(@"Error: %@", error);
}

- (void)locationManager:(CLLocationManager *)manager didUpdateLocations:(NSArray *)locations
{
    NSLog(@"Location: %@", [locations lastObject]);
    CLLocation *currentLocation = [locations lastObject];
    
    if (currentLocation != nil) {
        self.latitude = [NSString stringWithFormat:@"%.8f", currentLocation.coordinate.latitude];
        self.longitude = [NSString stringWithFormat:@"%.8f", currentLocation.coordinate.longitude];
    }
}

- (IBAction)sendRequest:(id)sender
{
    // Use geolocater to define current location
    [self getCurrentLocation];
    // Disable the refresh button during request
    [self.refreshButton setEnabled:NO];
    // Create the request
    //NSString *url = [NSString stringWithFormat:@"http://treasurely.no-ip.org:8000/treasures/%@/%@", self.latitude, self.longitude];
    NSString *base = [[NSUserDefaults standardUserDefaults] stringForKey:@"baseUrl"];
   // NSString *url = [NSString stringWithFormat:@"%@%s", base, "treasures/51.868809/5.737385"];
    NSString *url = [NSString stringWithFormat:@"%@%s/%@/%@", base, "treasures", self.latitude, self.longitude];
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
        self.nearbyTreasuresArray = [NSJSONSerialization JSONObjectWithData:_buffer options:kNilOptions error:&error];
        
        // Back to main queu
        dispatch_async(dispatch_get_main_queue(), ^{
            // Check for any error
            if (!error) {
                [self.tableView reloadData];
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
