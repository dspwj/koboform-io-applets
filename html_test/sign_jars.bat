REM password is 460stephens
REM all of this was figured out from: https://support.comodo.com/index.php?_m=knowledgebase&_a=viewarticle&kbarticleid=1072

jarsigner -storetype pkcs12 -keystore neil.hendrick@berkeley.edu.pfx kobo_fileIOApplets.jar "neil hendrick"
jarsigner -storetype pkcs12 -keystore neil.hendrick@berkeley.edu.pfx plugin.jar "neil hendrick"
