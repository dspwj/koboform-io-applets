REM password is "password"
keytool -genkey -keystore myKeyStore -alias hrc_kobo
keytool -selfcert -keystore myKeyStore -alias hrc_kobo

