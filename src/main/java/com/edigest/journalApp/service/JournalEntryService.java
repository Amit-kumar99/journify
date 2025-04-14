package com.edigest.journalApp.service;

import com.edigest.journalApp.entity.JournalEntry;
import com.edigest.journalApp.entity.User;
import com.edigest.journalApp.repository.JournalEntryRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@Slf4j
public class JournalEntryService {

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private UserService userService;


    @Transactional
    public void saveJournalEntry(JournalEntry journalEntry, String username) {
        try {
            User existingUser = userService.findByUsername(username);
            journalEntry.setDate(LocalDateTime.now());
            JournalEntry savedEntry = journalEntryRepository.save(journalEntry);
            existingUser.getJournalEntries().add(savedEntry);
            userService.saveUser(existingUser);
        }
        catch (Exception e) {
            log.error("Exception", e);
            throw new RuntimeException("Error occurred while saving the entry", e);
        }
    }

    public void saveJournalEntry(JournalEntry journalEntry) {
        try {
            journalEntryRepository.save(journalEntry);
        }
        catch (Exception e) {
            log.error("Exception", e);
        }
    }


    public Optional<JournalEntry> getJournalEntryById(ObjectId id) {
        return journalEntryRepository.findById(id);
    }

    @Transactional
    public boolean deleteEntryById(ObjectId entryId, String username) {
        boolean removed = false;
        try {
            User existingUser = userService.findByUsername(username);
            removed = existingUser.getJournalEntries().removeIf(entry -> entry.getId().equals(entryId));
            if(removed) {
                userService.saveUser(existingUser);
                journalEntryRepository.deleteById(entryId);
            }
        }
        catch(Exception e) {
            log.error("Exception", e);;
            throw new RuntimeException("An error occured while deleting the entry. ", e);
        }
        return removed;
    }

}
