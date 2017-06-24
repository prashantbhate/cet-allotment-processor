# cet-allotment-processor
This repo is used to process http://kea.kar.nic.in/cutoff/engg_cutoff_2016.pdf and sort the colleges by CET rank

# how to run locally

clone the repo and then run 

```$sh
./gradlew bootRun

```

navigate to 

http://localhost:4201 

This loads a embedded swagger UI with all exposed REST APIs

# access via heroku

This code is also hosted on heroku if you want to explore

https://cet-allotment-processor.herokuapp.com

Please note that this runs on free dyno so it 
goes to sleep after 30 min. also there is free quota of 550 hours each month
So if its all used up then you will have to wait till next month !!
 
