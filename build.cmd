if not exist "bin" mkdir "bin"
type NUL>build-args
echo -cp "lib/lwjgl2/lwjgl.jar;lib/lwjgl2/lwjgl_util.jar;">>build-args
echo -d bin>>build-args
echo -encoding UTF8>>build-args
dir src /a-d /b /s>>build-args
javac @build-args