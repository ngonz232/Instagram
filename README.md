# Project 4 - *FBU Instagram*

**Instagram** is a photo sharing app using Parse as its backend.

Time spent: **22** hours spent in total

## User Stories

The following **required** functionality is completed:

- [x] User sees app icon in home screen.
- [x] User can sign up to create a new account using Parse authentication
- [x] User can log in to his or her account
- [x] The current signed in user is persisted across app restarts
- [x] User can log out of his or her account
- [x] User can take a photo, add a caption, and post it to "Instagram"
- [x] User can view the last 20 posts submitted to "Instagram"
- [x] User can pull to refresh the last 20 posts submitted to "Instagram"
- [x] User can tap a post to go to a Post Details activity, which includes timestamp and caption.
- [x] User sees app icon in home screen

The following **stretch** features are implemented:

- [x] Style the login page to look like the real Instagram login page.
- [x] Style the feed to look like the real Instagram feed.
- [x] User can load more posts once he or she reaches the bottom of the feed using endless scrolling.
- [x] User should switch between different tabs using fragments and a Bottom Navigation View.
  - [x] Feed Tab (to view all posts from all users)
  - [x] Capture Tab (to make a new post using the Camera and Photo Gallery)
  - [x] Profile Tab (to view only the current user's posts, in a grid)
- [x] Show the username and creation time for each post
- User Profiles:
  - [x] Allow the logged in user to add a profile photo
  - [x] Display the profile photo with each post
  - [x] Tapping on a post's username or profile photo goes to that user's profile page
  - [x] User Profile shows posts in a grid
- [x] After the user submits a new post, show an indeterminate progress bar while the post is being uploaded to Parse
- [x] User can comment on a post and see all comments for each post in the post details screen.
- [x] User can like a post and see number of likes for each post in the post details screen.

The following **additional** features are implemented:

- [x] List anything else that you can get done to improve the app functionality!

1. The username text displayed is pulled from Parse directly. When there are multiple users,
it shows the username of the one currently logged in.

2. The name text displayed is pulled from Parse directly. When there are multiple users,
it shows the name of the one currently logged in.

3. I added a menu activity from which the user can log out or go back to thier profile. It can be
accessed from the toolbar.

4. I added a full sign up activity accessible from the login screen from which all data is persisted to parse 
including the person's full name to be displayed, the person's email, phone number, username, and password.

5. I completely designed the UI exactly like Instagram using custom graphics and icons as well as text placements.

6. When the user sucessfully submits a post, they are taken back to the main timeline to automatically see that post.

Overall, I added an immense amount of features outside both the scope of the MVP and optional stories to make it exactly like Instagram.

Please list two areas of the assignment you'd like to **discuss further with your peers** during the next class (examples include better ways to implement something, how to extend your app in certain ways, etc):

1. Fragments. I would love a workshop on this to better understand it as well as linear and coordinator layouts
2. Using the camera. I would also want reinforcement on this implementation since we only used it once and had very little instructions or a video for it.

## Video Walkthrough

Here's a walkthrough of implemented user stories:

<img src='/instagram.gif' />

GIF created with [Kap](https://getkap.co/).

## Credits

List an 3rd party libraries, icons, graphics, or other assets you used in your app.

- [Android Async Http Client](http://loopj.com/android-async-http/) - networking library


## Notes

Describe any challenges encountered while building the app.

The only challenge was working with fragments since we had never done so before. It took me a long time to thoroughly understand it, and I had to use outside resources
and guides heavily to learn. While Parse was new, I had already built a web app with a database before using MongoDB, so the learning curve was not much- especially 
since Parse has a UI which is extremely easy to use as opposed to MongoDB which I used in the terminal. Working with databases was fun.

## License

    Copyright 2021 Nicholas Gonzalez

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
