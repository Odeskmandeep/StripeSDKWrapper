Step 1. Add it in your settings.gradle.kts at the end of repositories:

	dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url = uri("https://jitpack.io") }
		}
	}

Step 2. Add the dependency

	implementation("com.github.Odeskmandeep:StripeSDKWrapper:LATEST_TAG_VERSION")

Step 3. Initialize the SDK in Applicaton Class

	StripeManager.init(this)

Step 4. Connect Stripe & it's connection callbacks:

	val callbacks = object: StripeCallbacks{
            override fun onSuccess(reader: a) {
                //Reader Connected Successfuly.
            }

            override fun onError(error: String) {
               
            }

            override fun onFinishInstallingUpdate(
                finished: Boolean?,
                error: String?
            ) {

            }

            override fun onStartInstallingUpdate(started: Boolean) {

            }

            override fun onReportReaderSoftwareUpdateProgress(progress: Float) {

            }

            override fun onReaderReconnectFailed(reader: a) {

            }

            override fun onReaderReconnectSucceeded(reader: a) {

            }
		}

		StripeManager.connectStripeIfPossible(
                mActivity = this,
                apiKey = null, //Important if not Required Backend dependency
                locationId = "LOCATION-ID", //Mandatory
                customURL = null, //If Backend Dependency Required
                optionalHeaderAuthorization = null,
                optionalHeaderAccept = null,
                bodyMetaDataHashMap = null, //Json Data (backend will recive this data in the Object of "meta_data" key)
                enableSimulation = false, //Simulator Enable/Disable
                callback = callbacks,
            )

Backend Instructions Mandatory (if implementing your own Apis):
- Request Method: POST
	
Backend will get request body like:

	 {
		"meta_data":{
		"Key": value
		}
	 }

Note: you don't need to send the "meta_data" key!

And Response should be like:

	{      
	"secret": "string" 
 	}

