const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

exports.setClassName = functions.https.onCall((data, context) => {
  const uid = data.uid;
  const className = data.className;

  // Chỉ cho phép người dùng admin thiết lập custom claims
  if (context.auth.token.admin !== true) {
    throw new functions.https.HttpsError(
        "permission-denied",
        "Bạn không có quyền thực hiện thao tác này.",
    );
  }

  return admin.auth().setCustomUserClaims(uid, {className: className})
      .then(() => ({
        message: `ClassName cho người dùng ${uid} cập nhật thành ${className}.`,
      }))
      .catch((error) => {
        throw new functions.https.HttpsError("internal", error.message);
      });
});
