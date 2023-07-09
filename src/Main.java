import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

class AssassinGameGUI extends JFrame {
    private JTextArea killRingTextArea;
    private JTextArea graveyardTextArea;
    private JButton killButton;
    private KillRing killRing; // Declare killRing as an instance variable
    private Graveyard graveyard; // Declare graveyard as an instance variable

    public AssassinGameGUI() {
        setTitle("Assassin Game");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        killRingTextArea = new JTextArea();
        killRingTextArea.setEditable(false);
        JScrollPane killRingScrollPane = new JScrollPane(killRingTextArea);
        mainPanel.add(killRingScrollPane, BorderLayout.CENTER);

        graveyardTextArea = new JTextArea();
        graveyardTextArea.setEditable(false);
        JScrollPane graveyardScrollPane = new JScrollPane(graveyardTextArea);
        mainPanel.add(graveyardScrollPane, BorderLayout.EAST);

        killButton = new JButton("Kill Player");
        killButton.setEnabled(false);
        killButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                killPlayer();
            }
        });
        mainPanel.add(killButton, BorderLayout.SOUTH);

        add(mainPanel);

        loadPlayers();
    }

    private void loadPlayers() {
        try {
            File file = new File("C:\\Users\\krueg\\Documents\\Zoom\\player_names.txt");
            Scanner sc = new Scanner(file);
            ArrayList<String> names = new ArrayList<String>();
            while (sc.hasNext()) {
                names.add(sc.nextLine());
            }
            sc.close();

            Player[] players = new Player[names.size()];
            for (int i = 0; i < names.size(); i++) {
                players[i] = new Player(names.get(i));
            }
            killRing = new KillRing(players);
            graveyard = new Graveyard();

            updateKillRingText();
            updateGraveyardText();

            killButton.setEnabled(true);
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "File not found: " + e.getMessage());
        }
    }

    private void killPlayer() {
        String name = JOptionPane.showInputDialog(null, "Enter the name of the next victim:");
        if (name != null && !name.isEmpty()) {
            Player victim = killRing.kill(name);
            if (victim == null) {
                JOptionPane.showMessageDialog(null, "That player is not in the kill ring.");
            } else {
                JOptionPane.showMessageDialog(null, victim.name + " has been killed!");
                graveyard.add(victim);
            }

            updateKillRingText();
            updateGraveyardText();

            if (killRing.head == killRing.head.next) {
                Player winner = killRing.head;
                JOptionPane.showMessageDialog(null, "The winner is " + winner.name + "!");
            }
        }
    }

    private void updateKillRingText() {
        if (killRing.head == null) {
            killRingTextArea.setText("Kill ring is empty!");
        } else {
            StringBuilder sb = new StringBuilder("Kill ring: ");
            Player temp = killRing.head;
            do {
                sb.append(temp.name).append(" ");
                temp = temp.next;
            } while (temp != killRing.head);
            killRingTextArea.setText(sb.toString());
        }
    }

    private void updateGraveyardText() {
        if (graveyard.head == null) {
            graveyardTextArea.setText("Graveyard is empty!");
        } else {
            StringBuilder sb = new StringBuilder("Graveyard: ");
            Player temp = graveyard.head;
            while (temp != null) {
                sb.append(temp.name).append(" ");
                temp = temp.next;
            }
            graveyardTextArea.setText(sb.toString());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                AssassinGameGUI gui = new AssassinGameGUI();
                gui.setVisible(true);
            }
        });
    }
}

class Player {
    String name;
    Player next;   //defines a player

    Player(String name) {
        this.name = name;
        this.next = null;
    }
}

class KillRing {
    Player head;

    KillRing(Player[] players) {
        this.head = null;
        shuffle(players);
        for (Player player : players) {  //method to add players
            add(player);
        }
    }

    private void shuffle(Player[] players) {
        Random rand = new Random();
        for (int i = players.length - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);   //method to shuffle the players into a random order
            Player temp = players[i];
            players[i] = players[j];
            players[j] = temp;
        }
    }

    private void add(Player player) {
        if (head == null) {
            head = player;
            head.next = head;
        } else {
            Player temp = head;
            do {
                if (temp.next == head) {
                    temp.next = player;   //adds the players to the kill ring and sets the head
                    player.next = head;
                    return;
                }
                temp = temp.next;
            } while (temp != head);
        }
    }

    void print() {
        if (head == null) {
            System.out.println("Kill ring is empty!");
            return;
        }
        System.out.print("Kill ring: ");
        Player temp = head;
        do {
            if (temp == null) {
                break;
            }
            System.out.print(temp.name + " ");   //Method to print if the kill ring is empty or prints out the remaining people in the kill ring
            temp = temp.next;
        } while (temp != head);
        System.out.println();
    }

    Player kill(String name) {
        if (head == null) {
            return null;
        }
        Player temp = head;
        Player prev = null;
        do {
            if (temp != null && temp.name.equalsIgnoreCase(name)) {
                if (prev == null) {
                    head = head.next;
                    Player last = head;
                    while (last.next != temp) {             //kills the player selected in the list and if the player selected is the head moves the head
                        last = last.next;
                    }
                    last.next = head;
                } else {
                    prev.next = temp.next;
                }
                temp.next = null;
                return temp;
            }
            prev = temp;
            temp = temp.next;
        } while (temp != head);
        return null;
    }
}
class Graveyard {
    Player head;

    void add(Player player) {
        if (head == null) {
            head = player;
        } else {
            Player temp = head;
            while (temp.next != null) {         //method to add and store people that are killed in the kill ring
                temp = temp.next;
            }
            temp.next = player;
        }
    }

    void print() {
        if (head == null) {
            System.out.println("Graveyard is empty!");
            return;
        }
        Player temp = head;
        System.out.print("Graveyard: ");        //method to call to print the players in the graveyard for each iteration.
        while (temp != null) {
            System.out.print(temp.name + " ");
            temp = temp.next;
        }
        System.out.println();
    }
}

