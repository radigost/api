#!/bin/bash

for i in $(eval echo {1..$1})
do
  curl "https://api.mockaroo.com/api/7ffe9280?count=1000&key=2395e2f0" >> "profile.csv" &
done
