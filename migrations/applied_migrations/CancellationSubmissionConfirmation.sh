#!/bin/bash

echo ""
echo "Applying migration CancellationSubmissionConfirmation"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /:lrn/cancellationSubmissionConfirmation                       controllers.CancellationSubmissionConfirmationController.onPageLoad(lrn: LocalReferenceNumber)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "cancellationSubmissionConfirmation.title = cancellationSubmissionConfirmation" >> ../conf/messages.en
echo "cancellationSubmissionConfirmation.heading = cancellationSubmissionConfirmation" >> ../conf/messages.en

echo "Migration CancellationSubmissionConfirmation completed"
