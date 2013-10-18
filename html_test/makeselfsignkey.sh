keytool -genkey -alias koboself -keystore koboSelfSignedKeystore -validity 365 < selfsignedkeystore.conf
jarsigner -keystore koboSelfSignedKeystore -storepass 2811WeltonStreet -keypass 2811WeltonStreet kobo_fileIOApplets.jar koboself
jarsigner -keystore koboSelfSignedKeystore -storepass 2811WeltonStreet -keypass 2811WeltonStreet plugin.jar koboself