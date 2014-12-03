package com.prairie.eemory.client.impl;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

import com.evernote.clients.NoteStoreClient;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.type.Note;
import com.evernote.thrift.TException;
import com.prairie.eemory.client.EDAMLimits;
import com.prairie.eemory.client.ENNote;
import com.prairie.eemory.client.NoteOps;
import com.prairie.eemory.client.StoreClientFactory;
import com.prairie.eemory.enml.ENML;
import com.prairie.eemory.exception.EDAMDataModel;
import com.prairie.eemory.exception.OutOfDateException;
import com.prairie.eemory.util.ListUtil;

public class NoteOpsTextImpl extends NoteOps {

    private final NoteStoreClient noteStoreClient;

    public NoteOpsTextImpl(final String token) throws EDAMUserException, EDAMSystemException, TException, OutOfDateException {
        noteStoreClient = StoreClientFactory.getInstance(token).getNoteStoreClient();
    }

    @Override
    public void updateOrCreate(final ENNote args) throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException, ParserConfigurationException, SAXException, IOException, DOMException, OutOfDateException {
        if (ListUtil.isNullOrEmptyList(args.getContent())) {
            return;
        }
        if (shouldUpdate(args)) {
            update(args);
        } else {
            create(args);
        }
    }

    private void create(final ENNote args) throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException, ParserConfigurationException, SAXException, IOException, OutOfDateException {
        Note note = new Note();
        note.setTitle(StringUtils.abbreviate(args.getName(), EDAMLimits.EDAM_NOTE_TITLE_LEN_MAX));
        if (StringUtils.isNotBlank(args.getNotebook().getGuid())) {
            note.setNotebookGuid(args.getNotebook().getGuid());
        }

        ENML enml = new ENML();
        enml.addComment(args.getComments());
        enml.addContent(args.getContent());

        note.setContent(enml.get());

        for (String tagName : args.getTags()) {
            tagName = tagName.trim();
            if (StringUtils.isNotBlank(tagName)) {
                note.addToTagNames(tagName);
            }
        }

        noteStoreClient.createNote(note);
    }

    private void update(final ENNote args) throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException, DOMException, ParserConfigurationException, SAXException, IOException, OutOfDateException {
        Note note = noteStoreClient.getNote(args.getGuid(), true, false, false, false);
        if (!note.isActive()) {
            EDAMNotFoundException e = new EDAMNotFoundException();
            e.setIdentifier(EDAMDataModel.Note_noteGuid.toString());
            e.setKey(args.getGuid());
            throw e;
        }
        note.unsetResources();

        // update content
        ENML enml = new ENML(note.getContent());
        enml.addComment(args.getComments());
        enml.addContent(args.getContent());

        note.setContent(enml.get());

        // update tags
        for (String tagName : args.getTags()) {
            tagName = tagName.trim();
            if (StringUtils.isNotBlank(tagName)) {
                note.addToTagNames(tagName);
            }
        }

        noteStoreClient.updateNote(note);
    }

    private boolean shouldUpdate(final ENNote args) {
        return StringUtils.isNotBlank(args.getGuid());
    }

}
