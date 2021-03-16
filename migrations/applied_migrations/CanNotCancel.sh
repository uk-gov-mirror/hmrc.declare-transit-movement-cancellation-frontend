#!/bin/bash

echo ""
echo "Applying migration CanNotCancel"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /:lrn/canNotCancel                       controllers.CanNotCancelController.onPageLoad(lrn: LocalReferenceNumber)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "canNotCancel.title = canNotCancel" >> ../conf/messages.en
echo "canNotCancel.heading = canNotCancel" >> ../conf/messages.en

echo "Migration CanNotCancel completed"
