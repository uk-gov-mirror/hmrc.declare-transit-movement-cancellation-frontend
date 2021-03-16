#!/bin/bash

echo ""
echo "Applying migration DepartureNotFound"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /:lrn/departureNotFound                       controllers.DepartureNotFoundController.onPageLoad(lrn: LocalReferenceNumber)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "departureNotFound.title = departureNotFound" >> ../conf/messages.en
echo "departureNotFound.heading = departureNotFound" >> ../conf/messages.en

echo "Migration DepartureNotFound completed"
