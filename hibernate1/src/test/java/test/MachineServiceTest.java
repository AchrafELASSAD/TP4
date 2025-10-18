package test;

import entities.Machine;
import entities.Salle;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import services.MachineService;
import services.SalleService;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class MachineServiceTest {

    private MachineService machineService;
    private Machine machine;
    private Salle salle;
    private SalleService salleService;

    @Before
    public void setUp() {
        machineService = new MachineService();
        salleService = new SalleService();

        // Créer et persister une salle
        salle = new Salle("A101");
        salleService.create(salle);

        // Créer et persister une machine
        machine = new Machine();
        machine.setRef("MACH-001");
        machine.setDateAchat(new Date());
        machine.setSalle(salle);

        machineService.create(machine);
    }

    @After
    public void tearDown() {
        // Supprimer la machine après chaque test si elle existe
        Machine foundMachine = machineService.findById(machine.getId());
        if (foundMachine != null) {
            machineService.delete(foundMachine);
        }

        // Supprimer la salle après chaque test si elle existe
        Salle foundSalle = salleService.findById(salle.getId());
        if (foundSalle != null) {
            salleService.delete(foundSalle);
        }
    }

    @Test
    public void testCreate() {
        assertNotNull("Machine should have been created with an ID", machine.getId());
        assertTrue("Machine ID should be greater than 0", machine.getId() > 0);
        assertNotNull("Machine should have a salle", machine.getSalle());
    }

    @Test
    public void testFindById() {
        Machine foundMachine = machineService.findById(machine.getId());
        assertNotNull("Machine should be found", foundMachine);
        assertEquals("Found machine ref should match", machine.getRef(), foundMachine.getRef());
        assertEquals("Found machine ID should match", machine.getId(), foundMachine.getId());
        assertNotNull("Found machine should have a salle", foundMachine.getSalle());
    }

    @Test
    public void testUpdate() {
        String oldRef = machine.getRef();
        machine.setRef("MACH-002");

        boolean result = machineService.update(machine);
        assertTrue("Machine should be updated successfully", result);

        Machine updatedMachine = machineService.findById(machine.getId());
        assertNotNull("Updated machine should exist", updatedMachine);
        assertEquals("Updated machine ref should match", "MACH-002", updatedMachine.getRef());
        assertNotEquals("Ref should have changed", oldRef, updatedMachine.getRef());
    }

    @Test
    public void testDelete() {
        int machineId = machine.getId();

        boolean result = machineService.delete(machine);
        assertTrue("Machine should be deleted successfully", result);

        Machine foundMachine = machineService.findById(machineId);
        assertNull("Machine should not be found after deletion", foundMachine);
    }

    @Test
    public void testFindAll() {
        List<Machine> machines = machineService.findAll();
        assertNotNull("Machines list should not be null", machines);
        assertTrue("Machines list should contain at least one machine", machines.size() > 0);

        // Vérifier que notre machine est dans la liste
        boolean found = false;
        for (Machine m : machines) {
            if (m.getId() == machine.getId()) {
                found = true;
                break;
            }
        }
        assertTrue("Created machine should be in the list", found);
    }

    @Test
    public void testFindBetweenDate() {
        // Créer une plage de dates : hier à demain
        Date yesterday = new Date(System.currentTimeMillis() - 86400000); // Hier
        Date tomorrow = new Date(System.currentTimeMillis() + 86400000);  // Demain

        List<Machine> machines = machineService.findBetweenDate(yesterday, tomorrow);

        assertNotNull("Machines list should not be null", machines);
        assertTrue("Machines list should contain at least one machine", machines.size() > 0);

        // Vérifier que notre machine est dans les résultats
        boolean found = false;
        for (Machine m : machines) {
            if (m.getId() == machine.getId()) {
                found = true;
                break;
            }
        }
        assertTrue("Created machine should be in the date range", found);
    }

    @Test
    public void testFindBetweenDateEmpty() {
        // Créer une plage de dates dans le futur où il n'y a pas de machines
        Date futureStart = new Date(System.currentTimeMillis() + 86400000 * 30); // Dans 30 jours
        Date futureEnd = new Date(System.currentTimeMillis() + 86400000 * 60);   // Dans 60 jours

        List<Machine> machines = machineService.findBetweenDate(futureStart, futureEnd);

        assertNotNull("Machines list should not be null", machines);
        assertEquals("Machines list should be empty", 0, machines.size());
    }

    @Test
    public void testMachineWithSalleRelation() {
        Machine foundMachine = machineService.findById(machine.getId());
        assertNotNull("Machine should have a salle", foundMachine.getSalle());
        assertEquals("Machine's salle should match", salle.getId(), foundMachine.getSalle().getId());
        assertEquals("Salle code should match", salle.getCode(), foundMachine.getSalle().getCode());
    }

    @Test
    public void testFindByIdNotFound() {
        Machine notFoundMachine = machineService.findById(99999);
        assertNull("Machine with non-existent ID should return null", notFoundMachine);
    }
}