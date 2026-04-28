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
                mainScope.launch {
                    Toast.makeText(this@MainActivity,"Stripe onSuccess: ${reader.readerName}",Toast.LENGTH_LONG).show()
                }
                Log.i("BADSHAH","Stripe onSuccess Called reader.readerName:${reader.readerName}")
            }

            override fun onError(error: String) {
                mainScope.launch {
                    Toast.makeText(
                        this@MainActivity,
                        "Stripe Error: $error",
                        Toast.LENGTH_LONG
                    ).show()
                }
                Log.i("BADSHAH","Stripe Error: $error")
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
