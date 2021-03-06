package de.schlangguru.paperlessuploader.ui.main

import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import de.schlangguru.paperlessuploader.R
import de.schlangguru.paperlessuploader.databinding.MainActivityBinding
import de.schlangguru.paperlessuploader.model.Document
import de.schlangguru.paperlessuploader.ui.settings.SettingsActivity
import kotlinx.android.synthetic.main.main_activity.*


class MainView : AppCompatActivity() {

    private val PERMISSION_REQ_READ_EXTERNAL_STORAGE = 1

    lateinit var vm: MainViewModel
    lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupBindings()
        setupEventObservers()
        setSupportActionBar(toolbar)
        processIntent()
        vm.connectPaperless()
    }

    private fun setupBindings() {
        vm = ViewModelProviders.of(this).get(MainViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.main_activity)
        binding.vm = vm
    }

    private fun setupEventObservers() {
        vm.showCorrespondentsEvent.observe(this, Observer { doc ->
            val correspondents = ArrayList<String>()
            correspondents.addAll(vm.correspondents.toTypedArray())
            correspondents.add("New...")

            AlertDialog.Builder(this)
                    .setTitle("Correspondents")
                    .setItems(correspondents.toTypedArray()) { dialog, itemIdx ->
                        doc?.let {
                            if (itemIdx == vm.correspondents.size) {
                                dialog.dismiss()
                                createNewCorrespondent(doc)
                            } else {
                                it.correspondent.set(vm.correspondents[itemIdx])
                            }
                        }
                    }
                    .show()
        })
    }

    private fun createNewCorrespondent(doc: Document) {
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT

        AlertDialog.Builder(this)
                .setTitle("New Correspondent")
                .setView(input)
                .setPositiveButton("OK", { _, _ -> doc.correspondent.set(input.text.toString()) })
                .setNegativeButton("Cancel", { dialog, _ -> dialog.cancel() })
                .show()
    }

    private fun processIntent() {
        when (intent.action) {
            Intent.ACTION_SEND -> enqueuePDFs(intent)
            Intent.ACTION_SEND_MULTIPLE -> enqueuePDFs(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.action_settings -> showSettings()
            R.id.action_refresh -> vm.connectPaperless()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun showSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    private fun enqueuePDFs(intent: Intent) {
        // Check Permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQ_READ_EXTERNAL_STORAGE)
        }
        // Has Permission
        else {
            for (i in 0 until intent.clipData.itemCount) {
                val uri = intent.clipData.getItemAt(i).uri
                vm.enqueueDocument(uri)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_REQ_READ_EXTERNAL_STORAGE) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                processIntent()
            } else {
                Snackbar.make(rootLayout, "Permission denied. Can't read documents from storage.", Snackbar.LENGTH_SHORT).show()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}
