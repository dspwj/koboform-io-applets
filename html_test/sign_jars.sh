# password is 14storystreet
# all of this was figured out from: https://support.comodo.com/index.php?_m=knowledgebase&_a=viewarticle&kbarticleid=1072
echo key phrase is "14storystreet"
jarsigner -storetype pkcs12 -keystore hendrick.neil@kobotoolbox.org.pfx -storepass 14storystreet kobo_fileIOApplets.jar  "neil hendrick"
jarsigner -storetype pkcs12 -keystore hendrick.neil@kobotoolbox.org.pfx -storepass 14storystreet plugin.jar  "neil hendrick"
