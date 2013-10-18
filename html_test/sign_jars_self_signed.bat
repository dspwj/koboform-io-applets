REM password is "password"

jarsigner -keystore myKeyStore kobo_fileIOApplets.jar hrc_kobo
jarsigner -keystore myKeyStore plugin.jar hrc_kobo
