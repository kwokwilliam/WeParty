# WeParty - INFO 448 Final Project

> Ali Rafiq, Emily Tao, Rio Ishii, William Kwok

Have you ever been to a party, gotten hella wasted, and then your friends worried about you going home on your own? Have you ever been too nauseated as you were going home, and found it hard to look at your phone to respond to all of your worried friends? Have you ever had to begrudgingly pull out your phone to answer swarms of texts because you are friends with literally everyone? Look no more, we have the solution for you!

WeParty is an app that allows party-goers to add information about a party and allow groups of friends to see their location. Users will be able to track others sharing their location, get notifications when specified users make it to a certain location, and notify others of their location. The app will emphasize usability and safety. It will also include an emergency call button that will allow users to quickly dial 911 in the event of an emergency. 

An existing solution is the “Find my Friends” app which is tailored more towards iOS devices, and also focuses more on just finding your friends. Our app is to be used in the context of a party.

## User Stories

* As a partier, I want to have the ability to choose which friend to share my location with because I do not want friends not invited to the party to see they have not been invited to the party.
* As a partier, I want to have the ability to notify close friends that I made home safely after attending a party so they know I am not dead.
* As a partier, I want to be able to track when I have made it home so that if I pass out once I get home, I do not have to tap a confirmation icon. Or, if I do not make it home after a certain time period, it will automatically alert my friends.
* As a partier, I want to have quick access to an emergency call button that will dial 911 so I have quick access to safety. 
* As a lightweight alcoholic, I want to be able to preset the app to track me, so I do not have to worry about it when I am hella faded.

## Mobile App vs. Desktop App/Website

This project works best as a native mobile app because users are most likely going to be on-the-go while using it. At parties, people usually have their phones, but are not likely to have their laptops or computers. We additionally want to take advantage of the built-in location sensor capabilities present on mobile phones. As a result, we believe that mobile devices are the most functional choice for this application. The sensor that we will be harnessing is the location sensor.

# Technologies

* We will use firebase to push live location data from our party-goer and pull this data on their friends’ phones.
* We will use location sensor data to detect where the user is, and if they are heading away from the party, we will start pushing the data to firebase
* We will use intents and notifications to display push notifications to the party-goer’s friends’ phones.

## Data privacy

When the user has reached home, we will clear their location off the database. We do not need to collect their data.
In the future, we can think about expanding this to collect their data anonymously and see where trips may have been cut short, so we can potentially investigate shady areas.

-----

## Database structure

```
UserIDsToName
	<userId>: Name
ActiveWatchList
	<userId>
		<rngPartyKey>: boolean
		<rngPartyKey>: boolean
ActiveUserParties
	<rngPartyKey>
		userId
		nameOfParty
		homeLocation
			lat
			lng
		partyLocation
			lat
			lng
		timeStart
		timeEnd
		emergencyCalled
		liveLocation
			lat
			lng
PastWatchList
	<userId>
		<rngPartyKey>: boolean
PastUserParties
	<rngPartyKey>
```