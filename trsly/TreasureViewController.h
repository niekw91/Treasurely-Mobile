//
//  TreasureViewController.h
//  trsly
//
//  Created by Niek Willems on 18/03/14.
//  Copyright (c) 2014 Niek Willems. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface TreasureViewController : UITableViewController <UITableViewDataSource, NSURLConnectionDataDelegate>

@property (strong, nonatomic) NSArray *nearbyTreasuresArray;

@property (weak, nonatomic) IBOutlet UIBarButtonItem *refreshButton;

- (IBAction)sendRequest:(id)sender;


@end
