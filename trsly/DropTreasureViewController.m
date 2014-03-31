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

@property (weak, nonatomic) IBOutlet UIBarButtonItem *dropButton;

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

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    
    manager = [[CLLocationManager alloc] init];
    geocoder = [[CLGeocoder alloc] init];
    
    [self.imageView setUserInteractionEnabled:YES];
    UITapGestureRecognizer *singleTap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(singleTapping:)];
    [singleTap setNumberOfTapsRequired:1];
    [self.imageView addGestureRecognizer:singleTap];
}

- (void)singleTapping:(UIGestureRecognizer *)recognizer
{
    UIView *subView = [[UIView alloc] initWithFrame:CGRectMake(90, 150, 150, 150)];
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
    [subView addSubview:selectImageButton];
    [subView addSubview:takeImageButton];
    [self.view addSubview:subView];
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
    
    [_params setObject:self.titleTextField.text forKey:@"title"];
    [_params setObject:self.messageTextField.text forKey:@"text"];
    [_params setObject:@"51.868809" forKey:@"latitude"];
    [_params setObject:@"5.737385" forKey:@"longitude"];
    
//    [_params setObject:self.latitude forKey:@"latitude"];
//    [_params setObject:self.longitude forKey:@"longitude"];
//    [_params setObject: forKey:@"user_id"];

    
    return _params;
}

- (IBAction)dropTreasureAction:(id)sender {
    // Use geolocater to define current location
    //[self getCurrentLocation];
    // Disable the refresh button during request
    [self.dropButton setEnabled:NO];
    // Create the request
    //NSString *url = [NSString stringWithFormat:@"http://treasurely.no-ip.org:8000/treasures/%@/%@", self.latitude, self.longitude];
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
    
    // Create connection
    self.connection = [NSURLConnection connectionWithRequest:request delegate:self];
    // Ensure connection was created
    if (self.connection) {
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
    // Re-enable drop button
    [self.dropButton setEnabled:YES];
    
    NSLog(@"Connection failed! Error - %@ %@",
          [error localizedDescription],
          [[error userInfo] objectForKey:NSURLErrorFailingURLStringErrorKey]);
}

- (void)connectionDidFinishLoading:(NSURLConnection *)connection
{
    // Re-enable drop button
    [self.dropButton setEnabled:YES];
    // Clear connection
    self.connection = nil;
}





@end
