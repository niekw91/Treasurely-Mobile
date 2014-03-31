//
//  TreasureDetailViewController.m
//  trsly
//
//  Created by Niek Willems on 18/03/14.
//  Copyright (c) 2014 Niek Willems. All rights reserved.
//

#import "TreasureDetailViewController.h"

@interface TreasureDetailViewController ()

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
	// Do any additional setup after loading the view.
    self.title = [self.treasureDetail objectForKey:@"title"];
    self.treasureTitle.text = [self.treasureDetail objectForKey:@"text"];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
