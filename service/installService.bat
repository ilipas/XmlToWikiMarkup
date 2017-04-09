set SERVICE_NAME=XmlToWikiService
set SERVICE_PATH=C:\Users\Ilija\Desktop\testService
set PR_INSTALL=C:\Users\Ilija\Desktop\testService\prunsrv.exe
 
REM Service log configuration
set PR_LOGPREFIX=%SERVICE_NAME%
set PR_LOGPATH=%SERVICE_PATH%
set PR_STDOUTPUT=%PR_LOGPATH%\stdout.txt
set PR_STDERROR=%PR_LOGPATH%\stderr.txt
set PR_LOGLEVEL=Debug
 
REM Path to java installation
set PR_JVM=C:\Program Files\Java\jre1.8.0_121\bin\server\jvm.dll
set PR_CLASSPATH=xmltowiki.jar
 
REM Startup configuration
set PR_STARTUP=auto
set PR_STARTMODE=jvm
set PR_STARTCLASS=com.ili.pas.main.MainService
set PR_STARTMETHOD=start
set PR_STARTPARAMS=input;output
 
REM Shutdown configuration
set PR_STOPMODE=jvm
set PR_STOPCLASS=com.ili.pas.main.MainService
set PR_STOPMETHOD=stop
 
REM JVM configuration
set PR_JVMMS=256
set PR_JVMMX=1024
set PR_JVMSS=4000
 
REM Install service 
%SERVICE_PATH%\prunsrv.exe //IS//%SERVICE_NAME%