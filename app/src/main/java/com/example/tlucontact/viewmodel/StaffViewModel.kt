import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tlucontact.data.model.Staff
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StaffViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _staffList = MutableStateFlow<List<Staff>>(emptyList())
    val staffList: StateFlow<List<Staff>> = _staffList

    init {
        fetchStaffs()
    }

    private fun fetchStaffs() {
        db.collection("staffs")
            .get()
            .addOnSuccessListener { result ->
                val list = result.documents.mapNotNull { doc ->
                    doc.toObject(Staff::class.java)?.copy(staffId = doc.id)
                }
                _staffList.value = list
            }
            .addOnFailureListener { exception ->
                println("🔥 Lỗi khi lấy dữ liệu: ${exception.message}")
            }


    }


}
