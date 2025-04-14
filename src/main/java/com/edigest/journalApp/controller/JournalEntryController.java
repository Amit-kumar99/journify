package com.edigest.journalApp.controller;

import com.edigest.journalApp.entity.JournalEntry;
import com.edigest.journalApp.entity.User;
import com.edigest.journalApp.service.JournalEntryService;
import com.edigest.journalApp.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/journal")
public class JournalEntryController {

    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private UserService userService;


    @GetMapping
    public ResponseEntity<List<JournalEntry>> getAllJournalEntriesByUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User existingUser = userService.findByUsername(username);
        List<JournalEntry> allEntries = existingUser.getJournalEntries();
        if (allEntries != null && !allEntries.isEmpty()) {
            return new ResponseEntity<>(allEntries, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<JournalEntry> createJournalEntryForUser(@RequestBody JournalEntry entry) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try {
            journalEntryService.saveJournalEntry(entry, username);
            return new ResponseEntity<>(entry, HttpStatus.CREATED);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/id/{entryId}")
    public ResponseEntity<JournalEntry> getJournalEntryById(@PathVariable ObjectId entryId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        List<JournalEntry> filteredEntriesList = user.getJournalEntries().stream().filter(entry -> entry.getId().equals(entryId)).collect(Collectors.toList());
        if(!filteredEntriesList.isEmpty()) {
            Optional<JournalEntry> existingJournalEntry = journalEntryService.getJournalEntryById(entryId);
            if (existingJournalEntry.isPresent()) {
                return new ResponseEntity<>(existingJournalEntry.get(), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/id/{entryId}")
    public ResponseEntity<?> deleteJournalEntryByIdForUser(@PathVariable ObjectId entryId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        boolean removed = journalEntryService.deleteEntryById(entryId, username);
        if(removed) {
            return new ResponseEntity<>("Entry deleted.", HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>("No such entry exists.", HttpStatus.NOT_FOUND);
    }

    @PutMapping("/id/{entryId}")
    public ResponseEntity<?> updateJournalEntryByIdForUser(@PathVariable ObjectId entryId, @RequestBody JournalEntry newEntry) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User existingUser = userService.findByUsername(username);
        List<JournalEntry> filteredEntrieslist = existingUser.getJournalEntries().stream().filter(entry -> entry.getId().equals(entryId)).collect(Collectors.toList());
        if (!filteredEntrieslist.isEmpty()) {
            Optional<JournalEntry> existingJournalEntry = journalEntryService.getJournalEntryById(entryId);
            if (existingJournalEntry.isPresent()) {
                JournalEntry entry = existingJournalEntry.get();
                entry.setTitle((!newEntry.getTitle().trim().isEmpty()) ? newEntry.getTitle() : entry.getTitle());
                entry.setContent((newEntry.getContent() != null && !newEntry.getContent().trim().isEmpty()) ? newEntry.getContent() : entry.getContent());
                journalEntryService.saveJournalEntry(entry);
                return new ResponseEntity<>(existingJournalEntry, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
