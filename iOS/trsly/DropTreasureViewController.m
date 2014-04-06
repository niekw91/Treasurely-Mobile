//
//  DropTreasureViewController.m
//  trsly
//
//  Created by Niek Willems on 16/03/14.
//  Copyright (c) 2014 Niek Willems. All rights reserved.
//

#import "DropTreasureViewController.h"
#import <CoreLocation/CoreLocation.h>

@interface DropTreasureViewController () <CLLocationManagerDelegate, UIImagePickerControllerDelegate, UINavigationControllerDelegate>

@property (weak, nonatomic) IBOutlet UIImageView *imageView;
@property (weak, nonatomic) IBOutlet UITextField *titleTextField;
@property (weak, nonatomic) IBOutlet UITextField *messageTextField;
@property (strong, nonatomic) UIView *imageMenuView;

@property (weak, nonatomic) IBOutlet UIBarButtonItem *dropButton;

@property (strong, nonatomic) NSMutableDictionary *dropReply;

@property (nonatomic, strong) NSMutableData *buffer;
@property (nonatomic, strong) NSURLConnection *connection;
@property (nonatomic, strong) NSMutableDictionary *params;

@property (nonatomic, strong) NSString *latitude;
@property (nonatomic, strong) NSString *longitude;

@end

@implementation DropTreasureViewController {
    CLLocationManager *manager;
    CLGeocoder *geocoder;
    CLPlacemark *placemark;
}

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];
    return YES;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Set UIToolbar invisible
    [self.navigationController setToolbarHidden:NO];
	// Do any additional setup after loading the view.
    self.dropReply = [[NSMutableDictionary alloc] init];
    // Set textfield delegates to collapse  on enter key
    self.titleTextField.delegate = self;
    self.messageTextField.delegate = self;
    
    manager = [[CLLocationManager alloc] init];
    geocoder = [[CLGeocoder alloc] init];
    // Initialize the  image menu view
    [self createImageMenuView];
    // Set border on imageview
    self.imageView.layer.borderColor = [UIColor lightGrayColor].CGColor;
    self.imageView.layer.borderWidth = 1.0f;
    self.imageView.layer.cornerRadius = 5.0;
    // Set single  tap gesture on imageview
    [self.imageView setUserInteractionEnabled:YES];
    UITapGestureRecognizer *singleTap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(singleTapping:)];
    [singleTap setNumberOfTapsRequired:1];
    [self.imageView addGestureRecognizer:singleTap];
    // Use geolocater to define current location
    [self getCurrentLocation];
}

- (void)createImageMenuView
{
    self.imageMenuView = [[UIView alloc] initWithFrame:CGRectMake(90, 150, 150, 150)];
    // Create subView buttons
    UIButton *selectImageButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
    [selectImageButton addTarget:self action:@selector(selectImagePressed:) forControlEvents:UIControlEventTouchUpInside];
    [selectImageButton setTitle:@"Select Image" forState:UIControlStateNormal];
    selectImageButton.frame = CGRectMake(0, 0, 150, 25);
    UIButton *takeImageButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
    [takeImageButton addTarget:self action:@selector(takeImagePressed:) forControlEvents:UIControlEventTouchUpInside];
    [takeImageButton setTitle:@"Take Image" forState:UIControlStateNormal];
    takeImageButton.frame = CGRectMake(0, 35, 150, 25);
    // Add buttons to subview
    [self.imageMenuView addSubview:selectImageButton];
    [self.imageMenuView addSubview:takeImageButton];
}

- (void)singleTapping:(UIGestureRecognizer *)recognizer
{
    if ([self.imageMenuView isDescendantOfView:self.view]) {
        [self.imageMenuView removeFromSuperview];
    } else {
        [self.view addSubview:self.imageMenuView];
    }
}

- (IBAction)selectImagePressed:(id)sender
{
    UIImagePickerController *picker = [[UIImagePickerController alloc] init];
    picker.delegate = self;
    picker.allowsEditing = YES;
    picker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
    
    [self presentViewController:picker animated:YES completion:NULL];
}

- (IBAction)takeImagePressed:(id)sender
{
    if (![UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypeCamera]) {
        UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:@"Error"
                                                            message:@"Device has no camera"
                                                           delegate:nil
                                                  cancelButtonTitle:@"OK"
                                                   otherButtonTitles:nil];
        [alertView show];
    } else {
        UIImagePickerController *picker = [[UIImagePickerController alloc] init];
        picker.delegate = self;
        picker.allowsEditing = YES;
        picker.sourceType = UIImagePickerControllerSourceTypeCamera;
    
        [self presentViewController:picker animated:YES completion:NULL];
    }
}

- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info
{
    UIImage *chosenImage = info[UIImagePickerControllerEditedImage];
    self.imageView.image = chosenImage;
    
    [picker dismissViewControllerAnimated:YES completion:NULL];
}

- (void)imagePickerControllerDidCancel:(UIImagePickerController *)picker
{
    [picker dismissViewControllerAnimated:YES completion:NULL];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)getCurrentLocation
{
    manager.delegate = self;
    manager.desiredAccuracy = kCLLocationAccuracyBest;
    
    [manager startUpdatingLocation];
}

#pragma mark Handle POST data

- (void)handlePostData
{
    if(self.imageView.image) {
        NSString *treasureId = [self.dropReply objectForKey:@"id"];
        if (treasureId) {
            [self uploadImage:treasureId];
        }
    } else {
        [self.navigationController popViewControllerAnimated:YES];
    }
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

#pragma mark POST Request Methods

- (NSMutableDictionary *)setRequestBody
{
    _params = [[NSMutableDictionary alloc] init];
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    
    [_params setObject:self.titleTextField.text forKey:@"title"];
    [_params setObject:self.messageTextField.text forKey:@"text"];
    //[_params setObject:@"51.868809" forKey:@"latitude"];
    //[_params setObject:@"5.737385" forKey:@"longitude"];
    
    [_params setObject:self.latitude forKey:@"latitude"];
    [_params setObject:self.longitude forKey:@"longitude"];
    [_params setObject:[defaults objectForKey:@"token"] forKey:@"user_id"];

    
    return _params;
}

- (IBAction)dropTreasureAction:(id)sender {
    // Use geolocater to define current location
    //[self getCurrentLocation];
    // Disable the refresh button during request
    [self.dropButton setEnabled:NO];
    // Create the request
    NSString *base = [[NSUserDefaults standardUserDefaults] stringForKey:@"baseUrl"];
    NSString *url = [NSString stringWithFormat:@"%@%s", base, "treasure"];
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
    
    [self createConnection:request];
}

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error
{
    // Clear connection and buffer
    self.connection = nil;
    // Re-enable drop button
    [self.dropButton setEnabled:YES];
    
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

- (void)createConnection:(NSMutableURLRequest *)request
{
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

- (void)connectionDidFinishLoading:(NSURLConnection *)connection
{
    // Dispatch from main queue for json processing
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        NSError *error = nil;
        self.dropReply = [NSJSONSerialization JSONObjectWithData:_buffer options:kNilOptions error:&error];
        
        // Back to main queu
        dispatch_async(dispatch_get_main_queue(), ^{
            // Check for any error
            if (!error) {
                [self handlePostData];
            } else {
                // Request failed
            }
            
            // Re-enable drop button
            [self.dropButton setEnabled:YES];
            // Clear connection and buffer
            self.connection = nil;
            self.buffer = nil;
        });
    });
}

- (void)uploadImage:(NSString *)treasureId
{
    NSString *base = [[NSUserDefaults standardUserDefaults] stringForKey:@"baseUrl"];
    NSString *url = [NSString stringWithFormat:@"%@%s", base, "upload"];
    NSURL *requestUrl = [NSURL URLWithString:url];
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:requestUrl
                                                                cachePolicy:NSURLRequestUseProtocolCachePolicy
                                                            timeoutInterval:60];
    
    [request setHTTPMethod:@"POST"];
    
    // Add a header field named Content-Type with a value
    NSString *boundary = @"----WebKitFormBoundarycC4YiaUFwM44F6rT";
    
    NSString *contentType = [NSString stringWithFormat:@"multipart/form-data; boundary=%@",boundary];
    
    [request addValue:contentType forHTTPHeaderField: @"Content-Type"];
    // end of what we've added to the header
    
    // the body of the post
    NSMutableData *body = [NSMutableData data];
    // Add treasure id to body
    [body appendData:[[NSString stringWithFormat:@"--%@\r\n", boundary] dataUsingEncoding:NSUTF8StringEncoding]];
    [body appendData:[@"Content-Disposition: form-data; name=\"id\"\r\n\r\n"
                      dataUsingEncoding:NSUTF8StringEncoding]];
    [body appendData:[treasureId  dataUsingEncoding:NSUTF8StringEncoding]];
    // Now we need to append the different data 'segments'. We first start by adding the boundary.
    [body appendData:[[NSString stringWithFormat:@"\r\n--%@\r\n",boundary] dataUsingEncoding:NSUTF8StringEncoding]];
	
    // Append the image
    [body appendData:[@"Content-Disposition: form-data; name=\"file\"; filename=\"iphone-upload.jpg\"\r\n" dataUsingEncoding:NSUTF8StringEncoding]];
    
    // We now need to tell the receiver what content type we have
    [body appendData:[@"Content-Type: image/jpeg\r\n\r\n" dataUsingEncoding:NSUTF8StringEncoding]];
    
    // Convert image to NSData
    NSData *imageData = [NSData dataWithData:UIImageJPEGRepresentation(self.imageView.image, 1.0)];
    // Append the actual image data
    [body appendData:[NSData dataWithData:imageData]];
    
    // Delimiting boundary
    [body appendData:[[NSString stringWithFormat:@"\r\n--%@--\r\n",boundary] dataUsingEncoding:NSUTF8StringEncoding]];
	
    // Add body to request
    [request setHTTPBody:body];
    
    NSURLConnection *connection = [[NSURLConnection alloc] initWithRequest:request
                                                                  delegate:NO
                                                          startImmediately:NO]; 
    
    [connection start];
    
    [self.navigationController popViewControllerAnimated:YES];
}


@end
