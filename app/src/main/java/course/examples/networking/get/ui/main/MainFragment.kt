package course.examples.networking.get.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import course.examples.networking.get.R

class MainFragment : Fragment() {

    // The viewModel is initialized in onCreateView().
    private lateinit var viewModel: MainViewModel

    /**
     * Called by framework to inflate and return the fragment view.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    /**
     * Called by the framework once the view has been created.
     * This is the appropriate place to update the view or add
     * listeners before the view is actually displayed.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Acquire access to activity ViewModel from framework ViewModelProvider.
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        // Get button instance from the view.
        val button = requireView().findViewById<Button>(R.id.button)!!
        val textView = requireView().findViewById<TextView>(R.id.textView)!!

        // Observer the viewModel's LiveData feed and display
        // each posted JSON String.
        viewModel.liveData.observe(viewLifecycleOwner) { result ->
            // Update the text view to display the JSON result.
            textView.text = result
        }

        // Set the button's OnClickListener to transparently
        // pass the button click event to the viewModel to handle.
        button.setOnClickListener {
            // Let the view model handle the button request.
            viewModel.onSendButtonClicked()
        }
    }

    companion object {
        /** Creates a new instance of this fragment. */
        fun newInstance() = MainFragment()
    }
}