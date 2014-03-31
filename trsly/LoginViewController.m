//
//  LoginViewController.m
//  trsly
//
//  Created by Niek Willems on 16/03/14.
//  Copyright (c) 2014 Niek Willems. All rights reserved.
//

#import "LoginViewController.h"
#import "TreasureViewController.h"

@interface LoginViewController ()

@property (strong, nonatomic) NSMutableDictionary *loginReply;

@property (weak, nonatomic) IBOutlet UITextField *emailTextField;
@property (weak, nonatomic) IBOutlet UITextField *passwordTextField;

@property (nonatomic, strong) NSMutableData *buffer;
@property (nonatomic, strong) NSURLConnection *connection;
@property (nonatomic, strong) NSMutableDictionary *params;

@property (weak, nonatomic) IBOutlet UILabel *label;

@end

@implementation LoginViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Hide back button
    self.navigationItem.hidesBackButton = YES;
	// Do any additional setup after loading the view.
    self.passwordTextField.delegate = self;
    self.loginReply = [[NSMutableDictionary alloc] init];
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    
    [defaults setObject:@"http://treasurely.no-ip.org:7000/" forKey:@"baseUrl"];
    [defaults synchronize];
    
    if([defaults objectForKey:@"token"]) {
        [self performSegueWithIdentifier:@"ShowTreasures" sender:self];
    }
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];
    return YES;
}

#pragma mark - Prepare for seque

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    if ([[segue identifier] isEqualToString:@"ShowTreasures"]) {
        TreasureViewController *treasureViewController = (TreasureViewController *)segue.destinationViewController;
    }
}
- (IBAction)action:(id)sender {
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    
    self.label.text = [defaults objectForKey:@"token"];
}

#pragma mark Handle POST data

- (void)handlePostData
{
    NSString *message = [self.loginReply objectForKey:@"message"];
    
    if ([message hasSuffix:@"succeeded"]) {
        NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
        
        [defaults setObject:[self.loginReply objectForKey:@"token"] forKey:@"token"];
        [defaults synchronize];
        // Perform segue
        [self performSegueWithIdentifier:@"ShowTreasures" sender:self];
    } else {
        UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:@"Error"
                                                            message:@"Authentication failed"
                                                           delegate:nil
                                                  cancelButtonTitle:@"OK"
                                                  otherButtonTitles:nil];
        [alertView show];
    }
}

#pragma mark POST request methods

- (NSMutableDictionary *)setRequestBody
{
    _params = [[NSMutableDictionary alloc] init];
    
    [_params setObject:self.emailTextField.text forKey:@"email"];
    [_params setObject:self.passwordTextField.text forKey:@"password"];
    
    return _params;
}

- (IBAction)sendLoginRequest:(id)sender
{
    // Disable the login button during request
    [self.loginButton setEnabled:NO];
    // Create the request
    NSString *base = [[NSUserDefaults standardUserDefaults] stringForKey:@"baseUrl"];
    NSString *url = [NSString stringWithFormat:@"%@%s", base, "login"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:url]];
    // Specify post request
    [request setHTTPMethod:@"POST"];
    // Set header fields
    [request setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    // Set request body
    [self setRequestBody];
    NSError *error = nil;
    NSData *requestBodyData = [NSJSONSerialization dataWithJSONObject:_params options:0 error:&error];
    [request setHTTPBody:requestBodyData];
    
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
    // Re-enable login button
    [self.loginButton setEnabled:YES];
    
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
        self.loginReply = [NSJSONSerialization JSONObjectWithData:_buffer options:kNilOptions error:&error];
        
        // Back to main queu
        dispatch_async(dispatch_get_main_queue(), ^{
            // Check for any error
            if (!error) {
                [self handlePostData];
            } else {
                // Request failed
            }
            
            // Re-enable refresh button
            [self.loginButton setEnabled:YES];
            // Clear connection and buffer
            self.connection = nil;
            self.buffer = nil;
        });
    });

}
    



@end
