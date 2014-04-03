//
//  LoginViewController.h
//  trsly
//
//  Created by Niek Willems on 16/03/14.
//  Copyright (c) 2014 Niek Willems. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface LoginViewController : UIViewController <NSURLConnectionDataDelegate, UITextFieldDelegate>

@property (weak, nonatomic) IBOutlet UIButton *loginButton;

- (IBAction)sendLoginRequest:(id)sender;

@end
