#include <jni.h>
#include <fcntl.h>

int fd = 0;
int fnd_fd = 0;
int pz_fd = 0;

JNIEXPORT jint JNICALL Java_com_example_jnidriver_JNIDriver_openDriver(JNIEnv * env, jclass class, jstring path) {
	jboolean iscopy;
	const char *path_utf = (*env)->GetStringUTFChars(env, path, &iscopy);
	fd = open(path_utf, O_WRONLY);
	(*env)->ReleaseStringUTFChars(env, path, path_utf);

	if (fd < 0) return -1;
	else return 1;
}

JNIEXPORT jint JNICALL Java_com_example_jnidriver_JNIDriver_openDriverFND(JNIEnv * env, jclass class, jstring path) {
	jboolean iscopy;
	const char *path_utf = (*env)->GetStringUTFChars(env, path, &iscopy);
	fnd_fd = open(path_utf, O_WRONLY);
	(*env)->ReleaseStringUTFChars(env, path, path_utf);

	if (fnd_fd < 0) return -1;
	else return 1;
}

JNIEXPORT jint JNICALL Java_com_example_jnidriver_JNIDriver_openDriverPZ(JNIEnv * env, jclass class, jstring path) {
	jboolean iscopy;
	const char *path_utf = (*env)->GetStringUTFChars(env, path, &iscopy);
	pz_fd = open(path_utf, O_WRONLY);
	(*env)->ReleaseStringUTFChars(env, path, path_utf);

	if (pz_fd < 0) return -1;
	else return 1;
}

JNIEXPORT void JNICALL Java_com_example_jnidriver_JNIDriver_closeDriver(JNIEnv * env, jobject obj) {
	if (fd > 0) close(fd);
}

JNIEXPORT void JNICALL Java_com_example_jnidriver_JNIDriver_closeDriverFND(JNIEnv * env, jobject obj) {
	if (fnd_fd > 0) close(fnd_fd);
}

JNIEXPORT void JNICALL Java_com_example_jnidriver_JNIDriver_closeDriverPZ(JNIEnv * env, jobject obj) {
	if (pz_fd > 0) close(pz_fd);
}

JNIEXPORT void JNICALL Java_com_example_jnidriver_JNIDriver_writeDriver(JNIEnv * env, jobject obj, jbyteArray arr, jint count) {
	jbyte* chars = (*env)->GetByteArrayElements(env, arr, 0);
	if (fd>0) write(fd, (unsigned char*)chars, count);
	(*env)->ReleaseByteArrayElements(env, arr, chars, 0);
}

JNIEXPORT void JNICALL Java_com_example_jnidriver_JNIDriver_writeDriverFND(JNIEnv * env, jobject obj, jbyteArray arr, jint count) {
	jbyte* chars = (*env)->GetByteArrayElements(env, arr, 0);
	if (fnd_fd>0) write(fnd_fd, (unsigned char*)chars, count);
	(*env)->ReleaseByteArrayElements(env, arr, chars, 0);
}

JNIEXPORT void JNICALL Java_com_example_jnidriver_JNIDriver_setBuzzer(JNIEnv * env, jobject obj, jchar c){
	int i = (int) c;
	write(pz_fd, &i, sizeof(i));
}
