//
//  TreasureDetailViewController.h
//  trsly
//
//  Created by Niek Willems on 18/03/14.
//  Copyright (c) 2014 Niek Willems. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface TreasureDetailViewController : UIViewController

@property (strong, nonatomic) NSDictionary *treasureDetail;

@property (weak, nonatomic) IBOutlet UILabel *treasureTitle;
@property (weak, nonatomic) IBOutlet UIImageView *treasureImage;

@end
