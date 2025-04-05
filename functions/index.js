/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

const {onRequest, onCall} = require("firebase-functions/v2/https");
const logger = require("firebase-functions/logger");
const admin = require('firebase-admin');
admin.initializeApp();

// Create and deploy your first functions
// https://firebase.google.com/docs/functions/get-started

// exports.helloWorld = onRequest((request, response) => {
//   logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });

exports.setClassName = onCall((data, context) => {
  const uid = data.uid; // ID của người dùng bạn muốn cập nhật custom claims
  const className = data.className; // className bạn muốn gán cho người dùng

  // Chỉ có admin mới có thể thực hiện việc này
  if (context.auth.token.admin !== true) {
    throw new functions.https.HttpsError('permission-denied', 'Bạn không có quyền thực hiện thao tác này.');
  }

  return admin.auth().setCustomUserClaims(uid, { className: className })
    .then(() => {
      return { message: `ClassName cho người dùng ${uid} đã được cập nhật thành ${className}.` };
    })
    .catch(error => {
      throw new functions.https.HttpsError('internal', error.message);
    });
});