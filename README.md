# Caloriya

This application requires API key and App ID from Yummly. In order to obtain both there are 3 important steps to follow. 

Step 1 : Visit https://developer.yummly.com and scroll down, click on “Request Academic Plan” to receive a free plan. After clicking on this button, fill out the form with the relevant email address and submit. 

Step 2: Visit https://developer.yummly.com/contact and fill out the form with the relevant email address. In the comments section, describe that you have submitted the request for the API key and ask them kindly to approve your request.

Step 3: Check your email to see if you received an approval (usually in about few hours after doing step 2 you will receive approval)



About Caloriya application:

Upon opening Caloriya it will load popular recipes. Clicking on “My recipes” will display favorized recipes (if any) from SQLite Database. Clicking back on “Popular Recipes”, it will load popular recipes (which may change). Please note that if a user has not favorized a recipe, a widget and “My recipes” link will not display any recipes.

Caloriya gives a user the option to filter popular recipes by the time needed to cook a meal from recipe.  
Caloriya also gives a user an option to calculate daily calories to eat. A user needs to enter his/her weight in pounds as well as pick the goal from the spinner. This result is an estimate and is based on active adult.

By clicking on the recipe, a user can see a details about the specific recipe and favorize it by clicking on thumbs up image. Once clicked on this image, the recipe will be added to the SQLite database which will be visible under “My Recipes” link and in widget. The thumbs up image will turn into thumbs down image which means that the user may now unfavorize the recipe and with that, remove it from the SQLite database and “My Recipes”/widget.
