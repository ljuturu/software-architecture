# software-architecture

Take-home assignment for Edmunds.com Software Architecture engineering candidates

## Before you start

1. Register for an API key to the [Edmunds.com Developer Network](http://developer.edmunds.com/).
1. Familiarise yourself with the [Edmunds.com APIs](http://edmunds.mashery.com/io-docs).
1. Install and configure Git. If Git is new to you, familiarise yourself with the following commands:
  * `git status`
  * `git add`
  * `git commit`

## Instructions

1. When you're ready, begin the assignment.
  * This directory contains a Git repository. Please commit your changes as you progress.
1. When you're done:
  * Delete any compiled binaries and artifacts of the build process (i.e. run `make clean` or the equivalent for your build system)
  * Zip up the repository (all source code including the `.git` directory).
  * Send the archive to your recruiter.

## Tasks

1. Design a RESTful API that takes as input, vehicle identification number (VIN) and zip code and returns (at minimum) the following:
  * The average consumer rating for the vehicle, if available (year, make, model granularity).
  * A brief summary of the Edmunds.com Editor review for the vehicle, if available (year, make, model granularity).
  * The mileage, if available (miles per gallon for both city and highway driving)
  * The engine fuel type, if available (e.g. gas, hybrid, or electric)
1. Implement the API.
  * Implement it as if it were to be deployed to production and called by 200,000 users per month.
  * Implement it in a way that is easily maintained and supported.
1. Document the API.

## Requirements

* `run.sh` must start the REST application.
  * The application should continue to run until the script is killed.
* The application must listen on port 8080.
* The application must run in a UNIX-like environment (e.g. Linux, Mac OS X, Cygwin).
* The application must be documented.
* The application must provide a way to provide the API key. Do not commit your own key to the repository.
* Complete this assignment as you would if you intend this to be deployed to production.
  * If there is something you would do or add to make it production ready, but you are unsure if it should be completed as part of this assignment, include that in the documentation. Specifically, point out any potential performance issues and how you would address them.

## You may assume

* The server running the application will have the following installed:
  * Java, Maven, Gradle
  * Python, pip, easyinstall
  * node, npm
  * Ruby, rbenv, rvm, bundle
  * make, cc
  * cabal, ghc
* The server running the application will be connected to the internet.
* The backing REST services may be unreliable.
* Data may not always be available.
