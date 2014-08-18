package com.prairie.eevernote.client.impl;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

import com.evernote.clients.NoteStoreClient;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteMetadata;
import com.evernote.edam.notestore.NotesMetadataList;
import com.evernote.edam.notestore.NotesMetadataResultSpec;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.Notebook;
import com.evernote.edam.type.Resource;
import com.evernote.edam.type.Tag;
import com.evernote.thrift.TException;
import com.prairie.eevernote.Constants;
import com.prairie.eevernote.client.EEClipper;
import com.prairie.eevernote.enml.ENML;
import com.prairie.eevernote.enml.Snippet;
import com.prairie.eevernote.exception.OutOfDateException;
import com.prairie.eevernote.util.EvernoteUtil;
import com.prairie.eevernote.util.FileUtil;
import com.prairie.eevernote.util.ListStringizer;
import com.prairie.eevernote.util.ListUtil;
import com.prairie.eevernote.util.MapStringizer;
import com.prairie.eevernote.util.StringUtil;

public class EEClipperImpl extends EEClipper {

    private NoteStoreClient noteStore;

    // parent Notebook is optional; if omitted, default notebook is used
    private String notebookGuid;
    // existing Note is optional; if omitted, create a new note; otherwise append
    private String noteGuid;
    private String tags;
    private String comments;

    private List<Notebook> notebooks;

    public EEClipperImpl(String token) throws TException, EDAMUserException, EDAMSystemException, OutOfDateException {
        noteStore = EvernoteUtil.getNoteStoreClient(token);
    }

    /**
     * Clip the file(s) as attachment to Evernote.
     *
     * @throws TException
     * @throws EDAMNotFoundException
     * @throws EDAMSystemException
     * @throws EDAMUserException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws TransformerException
     */
    @Override
    public void clipFile(List<File> files) throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, TransformerException {
        if (ListUtil.nullOrEmptyList(files)) {
            return;
        }
        if (shouldUpdateNote()) {
            updateNote(files);
        } else {
        	createNote(files);
        }
    }

    /**
     * Clip the selection to Evernote.
     *
     * @throws TException
     * @throws EDAMNotFoundException
     * @throws EDAMSystemException
     * @throws EDAMUserException
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws TransformerException
     *
     */
    @Override
    public void clipSelection(Snippet selection, String title) throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException, ParserConfigurationException, SAXException, IOException, TransformerException {
        if (selection.isNullOrEmpty()) {
            return;
        }
        if (shouldUpdateNote()) {
            updateNote(selection);
        } else {
            createNote(selection, title);
        }
    }

    private boolean shouldUpdateNote() {
        if (!StringUtil.nullOrEmptyOrBlankString(this.noteGuid)) {
            return true;
        }
        return false;
    }

    private void createNote(List<File> files) throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, TransformerException {
        Note note = new Note();
        note.setTitle(FileUtil.concatNameOfFiles(files));
        if (!StringUtil.nullOrEmptyOrBlankString(this.notebookGuid)) {
            note.setNotebookGuid(this.notebookGuid);
        }

        ENML enml = new ENML();
        enml.addComment(comments);

        for (File f : files) {
        	// create resource
            String mimeType = FileUtil.mimeType(f); // E.g "image/png"
            Resource resource = EvernoteUtil.createResource(f, mimeType);
            note.addToResources(resource);

            // create content
            String hashHex = FileUtil.bytesToHex(resource.getData().getBodyHash());
            enml.addResource(hashHex, mimeType);
        }

        note.setContent(enml.get());

        // create tags
        if (!StringUtil.nullOrEmptyOrBlankString(tags)) {
            note.setTagNames(ListUtil.arrayToList(tags.split(Constants.TAGS_SEPARATOR)));
        }

        noteStore.createNote(note);
    }

    private void createNote(Snippet selection, String noteTitle) throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException, ParserConfigurationException, SAXException, IOException, TransformerException {
        Note note = new Note();
        note.setTitle(noteTitle);
        if (!StringUtil.nullOrEmptyOrBlankString(this.notebookGuid)) {
            note.setNotebookGuid(this.notebookGuid);
        }

        ENML enml = new ENML();
        enml.addComment(comments);
        enml.addContent(selection);

        note.setContent(enml.get());

        if (!StringUtil.nullOrEmptyOrBlankString(tags)) {
            note.setTagNames(ListUtil.arrayToList(tags.split(Constants.TAGS_SEPARATOR)));
        }

        noteStore.createNote(note);
    }

    private void updateNote(List<File> files) throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, TransformerException {
        if (ListUtil.nullOrEmptyList(files)) {
            return;
        }

        Note note = noteStore.getNote(this.noteGuid, true, false, false, false);

        ENML enml = new ENML(note.getContent());
        // update content
        enml.addComment(comments);

        // update resource
        Iterator<File> iter = files.iterator();
        while (iter.hasNext()) {
            File file = iter.next();
            String mimeType = FileUtil.mimeType(file); // E.g "image/png"
            Resource resource = EvernoteUtil.createResource(file, mimeType);
            note.addToResources(resource);

            String hashHex = FileUtil.bytesToHex(resource.getData().getBodyHash());
            enml.addResource(hashHex, mimeType);
        }

        note.setContent(enml.get());

        // update tags
        if (!StringUtil.nullOrEmptyOrBlankString(tags)) {
            String[] tagNames = tags.split(Constants.TAGS_SEPARATOR);
            for (int i = 0; i < tagNames.length; i++) {
                note.addToTagNames(tagNames[i]);
            }
        }

        noteStore.updateNote(note);
    }

    private void updateNote(Snippet selection) throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException, DOMException, ParserConfigurationException, SAXException, IOException, TransformerException {
        Note note = noteStore.getNote(this.noteGuid, true, false, false, false);
        note.unsetResources();

        // update content
        ENML enml = new ENML(note.getContent());
        enml.addComment(comments);
        enml.addContent(selection);

        note.setContent(enml.get());

        // update tags
        if (!StringUtil.nullOrEmptyOrBlankString(tags)) {
            String[] tagNames = tags.split(Constants.TAGS_SEPARATOR);
            for (int i = 0; i < tagNames.length; i++) {
                note.addToTagNames(tagNames[i]);
            }
        }

        noteStore.updateNote(note);
    }

    /**
     * return a user's all notebooks.
     *
     * @return The user's notebooks, filtered by <code>filterString</code>.
     *
     * @throws TException
     * @throws EDAMSystemException
     * @throws EDAMUserException
     */
    @Override
    public Map<String, String> listNotebooks() throws EDAMUserException, EDAMSystemException, TException {
        // List the notes in the user's account
        notebooks = noteStore.listNotebooks();
        return ListUtil.toStringMap(notebooks, new MapStringizer() {
            @Override
            public String key(Object o) {
                return ((Notebook) o).getName();
            }

            @Override
            public String value(Object o) {
                return ((Notebook) o).getGuid();
            }
        });
    }

    /**
     * return a user's all notes inside certain notebook.
     *
     * @return The notes in the certain notebook, filtered by
     *         <code>filterString</code>.
     *
     * @throws TException
     * @throws EDAMNotFoundException
     * @throws EDAMSystemException
     * @throws EDAMUserException
     */
    @Override
    public Map<String, String> listNotesWithinNotebook(String notebookGuid) throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException {
        NotesMetadataList notesMetadataList = new NotesMetadataList();

        NoteFilter filter = new NoteFilter();
        if (!StringUtil.nullOrEmptyOrBlankString(notebookGuid)) {
            filter.setNotebookGuid(notebookGuid);
        }

        NotesMetadataResultSpec resultSpec = new NotesMetadataResultSpec();
        resultSpec.setIncludeTitle(true);

        notesMetadataList = noteStore.findNotesMetadata(filter, 0, com.evernote.edam.limits.Constants.EDAM_USER_NOTES_MAX, resultSpec);

        return ListUtil.toStringMap(notesMetadataList.getNotes(), new MapStringizer() {
            @Override
            public String key(Object o) {
                return ((NoteMetadata) o).getTitle() + Constants.LEFT_PARENTHESIS + ((NoteMetadata) o).getGuid() + Constants.RIGHT_PARENTHESIS;
            }

            @Override
            public String value(Object o) {
                return ((NoteMetadata) o).getGuid();
            }
        });
    }

    /**
     * return a user's all tags.
     */
    @Override
    public String[] listTags() throws Exception {
        return ListUtil.toStringArray(this.noteStore.listTags(), new ListStringizer() {
            @Override
            public String element(Object o) {
                return ((Tag) o).getName();
            }
        });
    }

    @Override
    public void setNotebookGuid(String notebookGuid) {
        this.notebookGuid = notebookGuid;
    }

    @Override
    public void setNoteGuid(String noteGuid) {
        this.noteGuid = noteGuid;
    }

    @Override
    public void setTags(String tags) {
        this.tags = tags;
    }

    @Override
    public void setComments(String comments) {
        this.comments = comments;
    }

}
