package ma.dexter.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.annotation.CallSuper
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.github.zawadz88.materialpopupmenu.MaterialPopupMenuBuilder
import io.github.rosemoe.sora.widget.CodeEditor
import ma.dexter.R
import ma.dexter.databinding.FragmentBaseCodeEditorBinding
import ma.dexter.editor.util.setDefaults
import ma.dexter.editor.util.setOnTextChangedListener
import ma.dexter.ui.viewmodel.MainViewModel
import ma.dexter.util.checkableItem

open class BaseCodeEditorFragment : BaseFragment() {
    private lateinit var binding: FragmentBaseCodeEditorBinding
    private val viewModel: MainViewModel by activityViewModels()

    protected lateinit var codeEditor: CodeEditor
    protected var isEdited = false

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBaseCodeEditorBinding.inflate(inflater, container, false)
        codeEditor = binding.codeEditor
        return binding.root
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        codeEditor.setDefaults()

        codeEditor.setOnTextChangedListener {
            isEdited = true
        }

        viewModel.viewPagerScrolled.observe(viewLifecycleOwner) {
            codeEditor.hideAutoCompleteWindow()
            codeEditor.textActionPresenter.onExit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_base_code_editor, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.it_undo -> codeEditor.undo()
            R.id.it_redo -> codeEditor.redo()
            R.id.it_save -> save()
            R.id.it_more -> showMoreMenu(requireActivity().findViewById(R.id.it_more))
        }

        return true
    }

    private fun save() {

    }

    @SuppressLint("SetTextI18n")
    private fun showMoreMenu(anchorView: View) {
        val builder = MaterialPopupMenuBuilder()

        beforeBuildMoreMenu(builder)
        builder.section {
            title = "Editor"

            item {
                label = "Search"
                iconDrawable =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_search_24)
                callback = {
                    codeEditor.beginSearchMode()
                }
            }

            customItem {
                checkableItem {
                    label = "Word wrap"
                    iconDrawable = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_baseline_wrap_text_24
                    )
                    checked = codeEditor.isWordwrap
                    callback = {
                        codeEditor.isWordwrap = it
                    }
                }
            }

            customItem {
                checkableItem {
                    label = "Auto complete"
                    iconDrawable = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_baseline_copyright_24
                    )
                    checked = codeEditor.isAutoCompletionEnabled
                    callback = {
                        codeEditor.isAutoCompletionEnabled = it
                    }
                }
            }
        }
        afterBuildMoreMenu(builder)

        builder.build()
            .show(requireContext(), anchorView)
    }

    protected open fun beforeBuildMoreMenu(popupMenuBuilder: MaterialPopupMenuBuilder) {
        // no-op
    }

    protected open fun afterBuildMoreMenu(popupMenuBuilder: MaterialPopupMenuBuilder) {
        // no-op
    }

}
