package hospital;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;

/**
 * Created by Marco Galassi (marco.galassi@unimore.it) on 02/12/15.
 */
public class Hospital2 implements Hospital {

    private String name;

    // Stupid handling, it may be done better (using some wrapper?)
    private EnumMap<EmergencyCode, Integer> waitingPatients; // tracks number of waiting patients per emergency code
    private EnumMap<EmergencyCode, Integer> queueingNumbers;
    private EnumMap<EmergencyCode, Integer> numberToBeAssisted;

    private HashSet<Doctor> doctors; // Hash set containing doctors' references. Notice that it is not populated in the constructor.

    public Hospital2(String name){

        this.name = name;
        waitingPatients = new EnumMap<EmergencyCode, Integer>(EmergencyCode.class);
        waitingPatients.put(EmergencyCode.G, 0);
        waitingPatients.put(EmergencyCode.Y, 0);
        waitingPatients.put(EmergencyCode.R, 0);

        queueingNumbers = new EnumMap<EmergencyCode, Integer>(EmergencyCode.class);
        queueingNumbers.put(EmergencyCode.G, 0);
        queueingNumbers.put(EmergencyCode.Y, 0);
        queueingNumbers.put(EmergencyCode.R, 0);

        numberToBeAssisted = new EnumMap<EmergencyCode, Integer>(EmergencyCode.class);
        numberToBeAssisted.put(EmergencyCode.G, 0);
        numberToBeAssisted.put(EmergencyCode.Y, 0);
        numberToBeAssisted.put(EmergencyCode.R, 0);

        doctors = new HashSet<Doctor>();

        printStatus();

    }

    /**
     * Assigns a random emergency code, distributed as follows: 60% of patients will be assigned green code, 25% will
     * be assigned yellow code, 15% will be assigned red code.
     * This method also assigns a queuing number relative to its priority to the patient, in order to allow a FIFO-like
     * handling of the waiting patients.
     * @param patient the patient that we need to assign an emergency code to.
     */
    private void assignEmergencyCode(Patient patient){
        EmergencyCode c;
        double t = Math.random(); // pseudo random in [0,1)
        if(t>0.85){
            c = EmergencyCode.R;
        }
        else if(t>0.60){
            c = EmergencyCode.Y;
        }
        else
            c = EmergencyCode.G;

        patient.setEmergencyCode(c);
        int currentNum = queueingNumbers.get(c);
        patient.setQueueingNumber(currentNum);
        currentNum++;
        queueingNumbers.put(c, currentNum);
    }

    /**
     * Checks whether there is a free doctor or not.
     * @return reference to a (any in particular) doctor, null otherwise
     */
    private Doctor getFreeDoctor(){
        for (Doctor doctor : doctors) {
            if(!doctor.isBusy())
                return doctor;
        }
        return null;
    }

    /**
     * Checks whether a patient may potentially be served. A patient may be served if there is not any other waiting
     * patient with higher priority (higher emergency code)
     * @param patient the patient we want to test
     * @return true if the patient can be server, false otherwise
     */
    private boolean isMyTurn(Patient patient) {
        patient.logln("checking if it is my turn");

        // if there are other patients with higher priorities
        if(patient.getEmergencyCode()==EmergencyCode.G &&
                (waitingPatients.get(EmergencyCode.Y)>0 || waitingPatients.get(EmergencyCode.R)>0)){
            return false;
        }
        if(patient.getEmergencyCode()==EmergencyCode.Y && waitingPatients.get(EmergencyCode.R)>0){
            return false;
        }

        // If i'm not the one who has the right to be server
        if(patient.getQueueingNumber() != numberToBeAssisted.get(patient.getEmergencyCode()))
            return false;

        return true;
    }

    /**
     * Checks whether there is already a waiting patient, used just for printing purposes.
     */
    private boolean areThereWaitingPatients(){
        for(EmergencyCode code: waitingPatients.keySet())
            if(waitingPatients.get(code)>0)
                return true;
        return false;
    }


    /**
     * Used by patient to gain access to therapies
     * @param patient
     * @throws InterruptedException
     */
    public synchronized void requestTherapy(Patient patient) throws InterruptedException {
        // Each patient is immediately assigned an emergency code, before any other thing.
        assignEmergencyCode(patient);
        patient.logln("assigned code "+patient.getEmergencyCode()+" and number "+patient.getQueueingNumber());

        EmergencyCode code = patient.getEmergencyCode();
        // The patient has now a code, and have to be served depending on it
        waitingPatients.put(code, waitingPatients.get(code)+1);
        printStatus();
        Doctor d = null;
        // need to wait if there is no free doctor, or there are free doctors but there are waiting patients with higher priorities
        while((d=getFreeDoctor())==null || !isMyTurn(patient)) {
            if(d==null)
                patient.logln("no free doctor, wait");
            else
                patient.logln("there is someone with higher emergency code than me or it's not my turn, wait");
            wait();
        }
        waitingPatients.put(code, waitingPatients.get(code)-1); // patient is no more waiting for a doctor
        int val = patient.getQueueingNumber();
        val += 1;
        numberToBeAssisted.put(code, val);

        patient.logln("doctor "+d.getName()+" will take care of me");
        d.setPatient(patient);

        printStatus();

        notifyAll(); // to wake up the doctor
        wait(); // wait for the doctor
        patient.logln("dismissed from hospital "+getHospitalName());
    }

    /**
     * Returns the hospital name
     * @return
     */
    public String getHospitalName(){
        return name;
    }

    private void printStatus(){
        // synchronize on System.out so output will appear in order, with no interspersed prints by other threads.
        synchronized (System.out){
            System.out.println("----- "+getHospitalName()+" -----");
            /*
             EnumMaps are sorted on the natural order of the keys, and this is reflected also by .keySet(). See
             https://docs.oracle.com/javase/7/docs/api/java/util/EnumMap.html for more information.
              */
            for(EmergencyCode code: waitingPatients.keySet()){
                System.out.println("Waiting\t["+code+"]:\t"+waitingPatients.get(code));
            }
            if(doctors.size()==0)
                System.out.println("No doctors yet");
            /*
             Iteration order on an HashSet is not guaranteed (see more at:
              http://docs.oracle.com/javase/7/docs/api/java/util/HashSet.html )
              */
            for(Doctor d: doctors){
                if(d.getPatient()==null)
                    System.out.println(d.getName()+" is free");
                else
                    System.out.println(d.getName()+" is taking care of "+d.getPatient().getName());
            }
            for(EmergencyCode code: numberToBeAssisted.keySet()){
                System.out.println("Next Patient\t["+code+"]:\t"+ numberToBeAssisted.get(code));
            }

            System.out.println("----- -----");
        }
    }

    /**
     * Used by doctors to take care of patients.
     * @param doctor
     */
    public synchronized void assistPatient(Doctor doctor) throws InterruptedException {
        doctors.add(doctor); // adds the doctor if not already present
        while(!doctor.isBusy()){ // until i'm not busy
            doctor.logln("not busy, wait");
            if(areThereWaitingPatients()) // to avoid waking up only doctors
                notifyAll(); // all the patients may be sleeping, notifyAll to wake them up
            wait();
        }
        doctor.logln("taking care of "+doctor.getPatient().getName());
    }

    /**
     * Used by doctors to dismiss patients.
     * @param doctor
     */
    public synchronized void dismissPatient(Doctor doctor){
        doctor.logln("dismissing patient "+doctor.getPatient().getName());
        doctor.setPatient(null); // to free the doctor
        notifyAll(); // to wake up other patients
    }

    /**
     * Main method.
     * @param args
     * @throws InterruptedException
     */
    public static void main(String args[]) throws InterruptedException {

        System.out.println("Start of the program, Hospital2");

        int nDocs = 3;
        int nNurses = 5;
        int nPatients = 10;

        Hospital hospital = new Hospital2("Rinceton-Plainsboro Teaching Hospital");

        // Create doctors
        for(int i=0; i<nDocs; i++){
            new Doctor("Doctor #"+i, hospital).start();
        }

        // create patients
        ArrayList<Patient> patients = new ArrayList<Patient>();
        for(int i=0; i<nPatients; i++){
            patients.add(new Patient("Patient #"+i, hospital));
        }

        Collections.shuffle(patients);
        for(Patient p: patients) {
            p.start();
            Thread.sleep((long)Math.random()*1000);
        }

        // wait patients to be done, then exit
        for(Patient p: patients)
            p.join();

        synchronized (System.out) {
            System.out.println("------------------------------------------> All patients done <------------------------------------------");
        }

        Thread.sleep(1000);
        System.exit(0);

    }

}
