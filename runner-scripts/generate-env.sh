DOT_ENV_PATH=/home/gitlab-runner/$1/.env

rm -f $DOT_ENV_PATH
echo "export DB_USERNAME=\"$DB_USERNAME\"" >> $DOT_ENV_PATH
echo "export DB_PASSWORD=\"$DB_PASSWORD\"" >> $DOT_ENV_PATH
echo "export EMAIL_USERNAME=\"$EMAIL_USERNAME\"" >> $DOT_ENV_PATH
echo "export EMAIL_PASSWORD=\"$EMAIL_PASSWORD\"" >> $DOT_ENV_PATH
echo "export EMAIL_HOST=\"$EMAIL_HOST\"" >> $DOT_ENV_PATH
echo "export EMAIL_PORT=\"$EMAIL_PORT\"" >> $DOT_ENV_PATH
echo "export GEOAPIFY_API_KEY=\"$GEOAPIFY_API_KEY\"" >> $DOT_ENV_PATH
echo "export MODERATION_KEY=\"$MODERATION_KEY\"" >> $DOT_ENV_PATH
echo "export WEATHER_KEY=\"$WEATHER_KEY\"" >> $DOT_ENV_PATH
