![Static Badge](https://img.shields.io/badge/Kotlin-Kotlin?label=Language&labelColor=purple) ![Static Badge](https://img.shields.io/badge/MIT-License?label=License&labelColor=blue) ![Static Badge](https://img.shields.io/badge/Android-Platform?label=Platform)</br> [![PlayStore](https://lh3.googleusercontent.com/q1k2l5CwMV31JdDXcpN4Ey7O43PxnjAuZBTmcHEwQxVuv_2wCE2gAAQMWxwNUC2FYEOnYgFPOpw6kmHJWuEGeIBLTj9CuxcOEeU8UXyzWJq4NJM3lg=s0)](https://play.google.com/store/apps/details?id=com.opsc.nestquest&pcampaignid=web_share)</br>


![Screenshot_157](https://github.com/HumanClone/NestQuest/assets/74468682/9a8c18b8-1970-44c0-a031-a6dae910ebb2)



## Features

- Account Creation
- View Hotspots in a user-determined radius
- View Details of a Hotspot
- View the Route to a Hotspot
- Navigate to Hotspot
>- Display Weather Conditions
>- Local Geo-Based Notifications
- Create an Observation with optional Picture Upload
- Navigate to an Observation
- Modal Views that have intutitve uses

the 2  highlighted features above were chosen and not part of the original requirement

***

# NestQuest
Discover the world of birds with NestQuest! Harnessing the power of the eBird API, NestQuest helps you locate and explore nearby birding hotspots on an interactive Google Map. Make observations at your current location, customize the render distance for hotspots, and choose your preferred measurement system for a personalized experience. With navigation features, easily find your way to a hotspot or observation point. Plus, enjoy the convenience of local notifications that alert you when you're near an exciting birding location. Dive into the avian world and enhance your birdwatching adventures with NestQuest!

## Login And Register

Here the user can login and register for the application 

![Login](https://github.com/HumanClone/NestQuest/assets/74468682/19e282e5-512c-4b17-9377-0a50f4c81569)

![Register](https://github.com/HumanClone/NestQuest/assets/74468682/4d3c9fbe-8e9d-417f-b805-f4bf8fa398b1)



## MapView 

when the user is authenicated they will be greeted with a Mapview and after granting permissions they 
will see there current location and the map will be populate with nearby hotspots(default system is metric with 10km radius)

![MapView](https://github.com/HumanClone/NestQuest/assets/74468682/cbcc5e41-bb41-4350-ba8a-ecbfad575500)

## Hotspots
if the user clicks on the hotspot button they will see a list of all the nearby hotspots according to there distance set 

![image](https://github.com/HumanClone/NestQuest/assets/74468682/c6d18866-82b0-4d89-8236-46f4d6bd8271)

if the user selects an item on the list or the user can click on the hotspot icon on the map to see a proper view of said hotspot and the map will move to that hotspot

![Hotspot View](https://github.com/HumanClone/NestQuest/assets/74468682/87925320-fc41-470b-93ad-df3892d2c317)

if the user cicks on the directions button they will be shown the directions to the hotspot as well as the route with the map updating according tot the users location 

![Directions](https://github.com/HumanClone/NestQuest/assets/74468682/676e1c0c-1344-4984-9431-0097d9f61d5f)

the user can go back to the mapview by pressing the exit navigation button

## Settings

Here the user can change thier measurement system as well 
as the maximum distance of hotspots 

![Settings](https://github.com/HumanClone/NestQuest/assets/74468682/a5ffb9dc-26a5-41b8-9a6a-f7853ca3cae1)

the user can also logout by clicking the logout button 

## Observation 

here the user can view a minimal detailed view of thier observations

![image](https://github.com/HumanClone/NestQuest/assets/74468682/70751aa8-a3b8-43f0-959c-158293118fde)


if they click on an item they will see a detailed view in which they can even navigate to the Observation by pressing the button


![ObservationView](https://github.com/HumanClone/NestQuest/assets/74468682/e454a748-e774-4ffc-9e04-70b094d90e8f)

if they click create observation they will be prompted to enter a description and an optional picture

![image](https://github.com/HumanClone/NestQuest/assets/74468682/66c324c5-a41f-4b61-936b-2ad760781b5d)


## NotificationAnd Back Ground Service Breakdown

To first activate the Background Service
>- the phone should allow the app to run unrestrited ( this is due to power saving options on some devices which auto end background services)
>- The user must have allowed the application to send notifications
>- the user must have allowed for the near a hotspot notification
>- the background checks run at least once every 3 hours with a maximum of once an hour( this is done to conserve battery as well as nto overwhelm the user with notifications if they are near a spot)
Note the user is not limited by the app if they do not wish t recieve notifications, the background service will not start and the app will have all of its other features

To trigger a notification
>- User must meet the above mentioned requirements
>- User must be less that 1000m away from the nearest hotspot,The notification will only trigger for the nearest
>- User must not have recieved a notification with in the last 3 hours( to void bombardment)
>- 

## Dependencies

This project makes use of the following:

- [Material Design V3](https://m3.material.io/)
- [Firebase](https://firebase.google.com/retr)
- [Retrofit2](https://square.github.io/retrofit/)
- [GSON](https://github.com/google/gson) 

***

## Requirements

>- Firebase authentiction
>- FireBase Cloud Storage
>- RetroFit
>- Google Maps API
>- eBird API
>- AccuWeather API
>- Android Tirimasu (API 33)
>- Google Servcies
</br>

## How to Run

>>Download APK provided
or
Download repository zip and open in Android Studio and build and run
>>or Download From Play Store
***

## Change Log

### Version 1.1


> - Fixed bugs that led to crashes
> - Added to ability to Navigate to an Observation
> - Visual redesign and updates
> - Added Geo-Based Local notifications
> - Shows Weather Conditions 


***

## End Notes

There are a few sections that were not covered for the sake of time such as validation, which is very extensive in the application and would have taken to much of the file o go into detail for.</br>
This was my first full fledged android project and was throuoghly enjoyable</br>

***

## License

```en
MIT License

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

```



