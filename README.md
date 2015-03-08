# swift

####Get Access Token:
Request URI: https://gateway.api.pcftest.com:9004/v1/oauth2/token?grant_type=client_credentials
Authorization Header: Basic RzIzcnlUMFpFMlRqRnJjbVU1Z2tqOVhkUTZqcXhaV0Q6eHlicEcxVTFwYmV3UmRIRw==    # Base64_encode(clientid:clientsecret)
Request Method: POST


#### Login for the user
Request URI: https://gateway.api.pcftest.com:9004/v1/oauth2/authorize/login
Authorization Header: Bearer wDJeOzjOGrwiPiMDO4g41Uvz5pNO           ##  Basic  access_token from previous request
Request Body: 
               {
				    "username": "nancy.anderson",
				    "password": "OneHabit,2beU"
				}				
Request Method: POST