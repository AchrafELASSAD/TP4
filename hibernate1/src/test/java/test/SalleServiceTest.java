package test;

import entities.Salle;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import services.SalleService;

import java.util.List;

import static org.junit.Assert.*;

public class SalleServiceTest {

    private SalleService salleService;
    private Salle salle;

    @Before
    public void setUp() {
        salleService = new SalleService();
        salle = new Salle();
        salle.setCode("A101");

        // Créer et persister la salle avant chaque test
        salleService.create(salle);
    }

    @After
    public void tearDown() {
        // Supprimer la salle après chaque test si elle existe
        Salle foundSalle = salleService.findById(salle.getId());
        if (foundSalle != null) {
            salleService.delete(foundSalle);
        }
    }

    @Test
    public void testCreate() {
        assertNotNull("Salle should have been created with an ID", salle.getId());
        assertTrue("Salle ID should be greater than 0", salle.getId() > 0);
    }

    @Test
    public void testFindById() {
        Salle foundSalle = salleService.findById(salle.getId());
        assertNotNull("Salle should be found", foundSalle);
        assertEquals("Found salle code should match", salle.getCode(), foundSalle.getCode());
        assertEquals("Found salle ID should match", salle.getId(), foundSalle.getId());
    }

    @Test
    public void testUpdate() {
        String oldCode = salle.getCode();
        salle.setCode("B202");

        boolean result = salleService.update(salle);
        assertTrue("Salle should be updated successfully", result);

        Salle updatedSalle = salleService.findById(salle.getId());
        assertNotNull("Updated salle should exist", updatedSalle);
        assertEquals("Updated salle code should match", "B202", updatedSalle.getCode());
        assertNotEquals("Code should have changed", oldCode, updatedSalle.getCode());
    }

    @Test
    public void testDelete() {
        int salleId = salle.getId();

        boolean result = salleService.delete(salle);
        assertTrue("Salle should be deleted successfully", result);

        Salle foundSalle = salleService.findById(salleId);
        assertNull("Salle should not be found after deletion", foundSalle);
    }

    @Test
    public void testFindAll() {
        List<Salle> salles = salleService.findAll();
        assertNotNull("Salles list should not be null", salles);
        assertTrue("Salles list should contain at least one salle", salles.size() > 0);

        // Vérifier que notre salle est dans la liste
        boolean found = false;
        for (Salle s : salles) {
            if (s.getId() == salle.getId()) {
                found = true;
                break;
            }
        }
        assertTrue("Created salle should be in the list", found);
    }

    @Test
    public void testFindByIdNotFound() {
        Salle notFoundSalle = salleService.findById(99999);
        assertNull("Salle with non-existent ID should return null", notFoundSalle);
    }
}