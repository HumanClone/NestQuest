![Static Badge](https://img.shields.io/badge/Kotlin-Kotlin?label=Language&labelColor=purple) ![Static Badge](https://img.shields.io/badge/MIT-License?label=License&labelColor=blue) ![Static Badge](https://img.shields.io/badge/Android-Platform?label=Platform)</br>

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

![image](https://github.com/HumanClone/NestQuest/assets/74468682/c94630c7-dc93-448f-a156-30d9e552146d)

![image](https://github.com/HumanClone/NestQuest/assets/74468682/3827aac9-c452-4a49-831e-2b095c593b2c)


## MapView 

when the user is authenicated they will be greeted with a Mapview and after granting permissions they 
will see there current location and the map will be populate with nearby hotspots(default system is metric with 10km radius)

![MapView](https://github.com/HumanClone/NestQuest/assets/74468682/e8f4f479-a1da-4b40-894d-b5f658a3898d)

## Hotspots
if the user clicks on the hotspot button they will see a list of all the nearby hotspots according to there distance set 

![image](https://github.com/HumanClone/NestQuest/assets/74468682/c6d18866-82b0-4d89-8236-46f4d6bd8271)

if the user selects an item on the list or the user can click on the hotspot icon on the map to see a proper view of said hotspot and the map will move to that hotspot

![Hotspot View](https://github.com/HumanClone/NestQuest/assets/74468682/fc751159-04a1-45e4-a00b-9fd310cae5b0)

if the user cicks on the directions button they will be shown the directions to the hotspot as well as the route with the map updating according tot the users location 

![Directions](https://github.com/HumanClone/NestQuest/assets/74468682/4ec09bb9-c01a-4e2d-8cb4-215bb4d5dec3)

the user can go back to the mapview by pressing the exit navigation button

## Settings

Here the user can change thier measurement system as well as the maximum distance of hotspots 

![Settings](https://github.com/HumanClone/NestQuest/assets/74468682/cd9a4303-1e6f-4998-996b-7dfa70720dad)

the user can also logout by clicking the logout button 

## Observation 

here the user can view a minimal detailed view of thier observations

![image](https://github.com/HumanClone/NestQuest/assets/74468682/70751aa8-a3b8-43f0-959c-158293118fde)


if they click on an item they will see a detailed view in which they can even navigate to the Observation by pressing the button

![ObservationView](https://github.com/HumanClone/NestQuest/assets/74468682/5a68f2d4-d9cd-467e-a4e2-178f9c2c2e9b)


if they click create observation they will be prompted to enter a description and an optional picture

![image](https://github.com/HumanClone/NestQuest/assets/74468682/66c324c5-a41f-4b61-936b-2ad760781b5d)


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



