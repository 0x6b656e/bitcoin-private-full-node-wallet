package org.btcprivate.wallets.fullnode.ui;

import org.btcprivate.wallets.fullnode.daemon.BTCPClientCaller;
import org.btcprivate.wallets.fullnode.daemon.BTCPClientCaller.NetworkAndBlockchainInfo;
import org.btcprivate.wallets.fullnode.daemon.BTCPClientCaller.WalletCallException;
import org.btcprivate.wallets.fullnode.daemon.BTCPInstallationObserver;
import org.btcprivate.wallets.fullnode.daemon.BTCPInstallationObserver.DAEMON_STATUS;
import org.btcprivate.wallets.fullnode.daemon.BTCPInstallationObserver.DaemonInfo;
import org.btcprivate.wallets.fullnode.daemon.BTCPInstallationObserver.InstallationDetectionException;
import org.btcprivate.wallets.fullnode.messaging.MessagingPanel;
import org.btcprivate.wallets.fullnode.util.BackupTracker;
import org.btcprivate.wallets.fullnode.util.Log;
import org.btcprivate.wallets.fullnode.util.OSUtil;
import org.btcprivate.wallets.fullnode.util.OSUtil.OS_TYPE;
import org.btcprivate.wallets.fullnode.util.StatusUpdateErrorReporter;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/*
 * This Java source file was generated by the Gradle 'init' task.
 */
public class BTCPWalletUI extends JFrame {
    private static final String VERSION = "1.0.5";
    private BTCPInstallationObserver installationObserver;
    private BTCPClientCaller clientCaller;
    private StatusUpdateErrorReporter errorReporter;

    private WalletOperations walletOps;

    private JMenuItem menuItemExit;
    private JMenuItem menuItemAbout;
    private JMenuItem menuItemShowPrivateKey;
    private JMenuItem menuItemImportOnePrivateKey;
    private JMenuItem menuItemOwnIdentity;
    private JMenuItem menuItemExportOwnIdentity;
    private JMenuItem menuItemImportContactIdentity;
    private JMenuItem menuItemAddMessagingGroup;
    private JMenuItem menuItemRemoveContactIdentity;
    private JMenuItem menuItemMessagingOptions;
    private JMenuItem menuItemShareFileViaIPFS;

    private DashboardPanel dashboard;
    private AddressesPanel addresses;
    private SendCashPanel sendPanel;
    private AddressBookPanel addressBookPanel;
    private MessagingPanel messagingPanel;



    JTabbedPane tabs;

    public BTCPWalletUI(StartupProgressDialog progressDialog,String title)
            throws IOException, InterruptedException, WalletCallException {
        super(title);

        if (progressDialog != null) {
            progressDialog.setProgressText("Starting wallet GUI...");
        }

        ClassLoader cl = this.getClass().getClassLoader();

        this.setIconImage(new ImageIcon(cl.getResource("images/btcp-200.png")).getImage());
        Container contentPane = this.getContentPane();

        errorReporter = new StatusUpdateErrorReporter(this);
        installationObserver = new BTCPInstallationObserver(OSUtil.getProgramDirectory());
        clientCaller = new BTCPClientCaller(OSUtil.getProgramDirectory());

        if (installationObserver.isOnTestNet()) {
            this.setTitle(this.getTitle() + " [TESTNET]");
        }

        // Build content
        tabs = new JTabbedPane();
        Font oldTabFont = tabs.getFont();
        Font newTabFont = new Font(oldTabFont.getName(), Font.BOLD, oldTabFont.getSize() * 57 / 50);
        tabs.setFont(newTabFont);
        BackupTracker backupTracker = new BackupTracker(this);

        tabs.addTab("Transactions ",
                new ImageIcon(cl.getResource("images/overview.png")),
                dashboard = new DashboardPanel(this, installationObserver, clientCaller,
                        errorReporter, backupTracker));
        tabs.addTab("My Addresses ",
                new ImageIcon(cl.getResource("images/own-addresses.png")),
                addresses = new AddressesPanel(this, clientCaller, errorReporter));
        tabs.addTab("Send BTCP ",
                new ImageIcon(cl.getResource("images/send.png")),
                sendPanel = new SendCashPanel(clientCaller, errorReporter, installationObserver, backupTracker));
        tabs.addTab("Address Book ",
                new ImageIcon(cl.getResource("images/address-book.png")),
                addressBookPanel = new AddressBookPanel(sendPanel, tabs));
        tabs.addTab("Messaging ",
                new ImageIcon(cl.getResource("images/messaging.png")),
                messagingPanel = new MessagingPanel(this, sendPanel, tabs, clientCaller, errorReporter));
        contentPane.add(tabs);

        this.walletOps = new WalletOperations(
                this, tabs, dashboard, addresses, sendPanel,
                installationObserver, clientCaller, errorReporter, backupTracker);

        int width = 870;

        OS_TYPE os = OSUtil.getOSType();

        // Window needs to be larger on Mac/Windows - typically
        if ((os == OS_TYPE.WINDOWS) || (os == OS_TYPE.MAC_OS)) {
            width += 100;
        }

        this.setSize(new Dimension(width, 440));

        // Build menu
        JMenuBar mb = new JMenuBar();
        JMenu file = new JMenu("Main");
        file.setMnemonic(KeyEvent.VK_M);
        int accelaratorKeyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        file.add(menuItemAbout = new JMenuItem("About", KeyEvent.VK_T));
        menuItemAbout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, accelaratorKeyMask));
        file.addSeparator();
        file.add(menuItemExit = new JMenuItem("Quit", KeyEvent.VK_Q));
        menuItemExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, accelaratorKeyMask));
        mb.add(file);

        JMenu wallet = new JMenu("Wallet");
        wallet.setMnemonic(KeyEvent.VK_W);

		/*
         * Disabled since there is no way of importing the wallet so does not make sense for Avg. Joe to export
		 */
		/*
		 * wallet.add(menuItemBackup = new JMenuItem("Backup", KeyEvent.VK_B));
		 * menuItemBackup.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, accelaratorKeyMask));
		 */

        //comment out since was disabled in the first place
        //wallet.add(menuItemEncrypt = new JMenuItem("Encrypt", KeyEvent.VK_E));
        //menuItemEncrypt.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, accelaratorKeyMask));
		/*
		 * disable since importing in BULK gives these issues:
		 * https://github.com/zcash/zcash/issues/2486
		 * https://github.com/zcash/zcash/issues/2524
		 */

		/*
		wallet.add(menuItemExportKeys = new JMenuItem("Export Private Keys", KeyEvent.VK_K));
		menuItemExportKeys.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, accelaratorKeyMask));
		wallet.add(menuItemImportKeys = new JMenuItem("Import Private Keys", KeyEvent.VK_I));
		menuItemImportKeys.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, accelaratorKeyMask));
		 */
        wallet.add(menuItemShowPrivateKey = new JMenuItem("View One Private Key", KeyEvent.VK_P));
        menuItemShowPrivateKey.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, accelaratorKeyMask));
        wallet.add(menuItemImportOnePrivateKey = new JMenuItem("Import One Private Key", KeyEvent.VK_N));
        menuItemImportOnePrivateKey.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, accelaratorKeyMask));

		/*
		 * wallet.add(menuItemExportToArizen = new JMenuItem("Export to Arizen wallet...", KeyEvent.VK_A));
		 * menuItemExportToArizen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, accelaratorKeyMask));
		 */
        mb.add(wallet);

        JMenu messaging = new JMenu("Messaging");
        messaging.setMnemonic(KeyEvent.VK_S);
        messaging.add(menuItemOwnIdentity = new JMenuItem("My Identity", KeyEvent.VK_D));
        menuItemOwnIdentity.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, accelaratorKeyMask));
        messaging.add(menuItemExportOwnIdentity = new JMenuItem("Export My Identity", KeyEvent.VK_X));
        menuItemExportOwnIdentity.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, accelaratorKeyMask));

        messaging.add(menuItemImportContactIdentity = new JMenuItem("Import Contact", KeyEvent.VK_Y));
        menuItemImportContactIdentity.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, accelaratorKeyMask));
        messaging.add(menuItemRemoveContactIdentity = new JMenuItem("Remove Contact", KeyEvent.VK_R));
        menuItemRemoveContactIdentity.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, accelaratorKeyMask));

        messaging.add(menuItemAddMessagingGroup = new JMenuItem("Create Group", KeyEvent.VK_G));
        menuItemAddMessagingGroup.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, accelaratorKeyMask));

        messaging.add(menuItemMessagingOptions = new JMenuItem("Options", KeyEvent.VK_O));
        menuItemMessagingOptions.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, accelaratorKeyMask));

        JMenu shareFileVia = new JMenu("Share file via:");
        shareFileVia.setMnemonic(KeyEvent.VK_V);
        // TODO: uncomment this for IPFS integration
        //messaging.add(shareFileVia);
        shareFileVia.add(menuItemShareFileViaIPFS = new JMenuItem("IPFS", KeyEvent.VK_F));
        menuItemShareFileViaIPFS.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, accelaratorKeyMask));

        mb.add(messaging);

        // TODO: Temporarily disable encryption until further notice - Oct 24 2016
        //menuItemEncrypt.setEnabled(false);

        this.setJMenuBar(mb);

        // Add listeners etc.
        menuItemExit.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        BTCPWalletUI.this.exitProgram();
                    }
                }
        );

        menuItemAbout.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            AboutDialog ad = new AboutDialog(BTCPWalletUI.this);
                            ad.setVisible(true);
                        } catch (UnsupportedEncodingException uee) {
                            Log.error("Unexpected error: ", uee);
                            BTCPWalletUI.this.errorReporter.reportError(uee);
                        }
                    }
                }
        );
        menuItemShowPrivateKey.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        BTCPWalletUI.this.walletOps.showPrivateKey();
                    }
                }
        );

        menuItemImportOnePrivateKey.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        BTCPWalletUI.this.walletOps.importSinglePrivateKey();
                    }
                }
        );

        menuItemOwnIdentity.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        BTCPWalletUI.this.messagingPanel.openOwnIdentityDialog();
                    }
                }
        );

        menuItemExportOwnIdentity.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        BTCPWalletUI.this.messagingPanel.exportOwnIdentity();
                    }
                }
        );

        menuItemImportContactIdentity.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        BTCPWalletUI.this.messagingPanel.importContactIdentity();
                    }
                }
        );

        menuItemAddMessagingGroup.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        BTCPWalletUI.this.messagingPanel.addMessagingGroup();
                    }
                }
        );

        menuItemRemoveContactIdentity.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        BTCPWalletUI.this.messagingPanel.removeSelectedContact();
                    }
                }
        );

        menuItemMessagingOptions.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        BTCPWalletUI.this.messagingPanel.openOptionsDialog();
                    }
                }
        );

        menuItemShareFileViaIPFS.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        BTCPWalletUI.this.messagingPanel.shareFileViaIPFS();
                    }
                }
        );

		/*
		 * menuItemExportToArizen.addActionListener(

				new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						BTCPWalletUI.this.walletOps.exportToArizenWallet();
					}
				}
				);
		 */

        // Close operation
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                BTCPWalletUI.this.exitProgram();
            }
        });

        // Show initial message
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    String userDir = OSUtil.getSettingsDirectory();
                    File warningFlagFile = new File(userDir + File.separator + "initialInfoShown_0.75.flag");
                    if (warningFlagFile.exists()) {
                        return;
                    } else {
                        warningFlagFile.createNewFile();
                    }

                } catch (IOException ioe) {
					/* TODO: report exceptions to the user */
                    Log.error("Unexpected error: ", ioe);
                }

                JOptionPane.showMessageDialog(
                        BTCPWalletUI.this.getRootPane().getParent(),
                        "The Bitcoin Private Full-Node Desktop Wallet is currently considered experimental. Use of this software\n" +
                                "comes at your own risk! Be sure to read the list of known issues and limitations\n" +
                                "at this page: https://github.com/BTCPrivate/bitcoin-private-full-node-wallet\n\n" +
                                "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR\n" +
                                "IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,\n" +
                                "FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE\n" +
                                "AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER\n" +
                                "LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,\n" +
                                "OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN\n" +
                                "THE SOFTWARE.\n\n" +
                                "(This message will only be shown once per release)",
                        "Disclaimer", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Finally dispose of the progress dialog
        if (progressDialog != null) {
            progressDialog.doDispose();
        }

        // Notify the messaging TAB that it is being selected - every time
        tabs.addChangeListener(
                new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        JTabbedPane tabs = (JTabbedPane) e.getSource();
                        if (tabs.getSelectedIndex() == 4) {
                            BTCPWalletUI.this.messagingPanel.tabSelected();
                        }
                    }
                }
        );

    }

    public void exitProgram() {
        Log.info("Exiting...");

        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        this.dashboard.stopThreadsAndTimers();
        this.addresses.stopThreadsAndTimers();
        this.sendPanel.stopThreadsAndTimers();
        this.messagingPanel.stopThreadsAndTimers();

        BTCPWalletUI.this.setVisible(false);
        BTCPWalletUI.this.dispose();

        System.exit(0);
    }

    public static void main(String argv[])
            throws IOException {

        String title = "Bitcoin Private Desktop GUI Wallet ";
        title = title.concat(VERSION);
        try {
            OS_TYPE os = OSUtil.getOSType();

            if ((os == OS_TYPE.WINDOWS) || (os == OS_TYPE.MAC_OS)) {
                possiblyCreateZENConfigFile();
            }

            Log.info("Bitcoin Private Full-Node Desktop Wallet (GUI, made in Java & Swing)");
            Log.info("OS: " + System.getProperty("os.name") + " = " + os);
            Log.info("Current directory: " + new File(".").getCanonicalPath());
            Log.info("Class path: " + System.getProperty("java.class.path"));
            Log.info("Environment PATH: " + System.getenv("PATH"));

            // Look and feel settings - a custom OS-look and feel is set for Windows
            if (os == OS_TYPE.WINDOWS) {
                // Custom Windows L&F and font settings
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");

                // This font looks good but on Windows 7 it misses some chars like the stars...
                //FontUIResource font = new FontUIResource("Lucida Sans Unicode", Font.PLAIN, 11);
                //UIManager.put("Table.font", font);
            } else if (os == OS_TYPE.MAC_OS) {
                // The MacOS L&F is active by default - the property sets the menu bar Mac style
                System.setProperty("apple.laf.useScreenMenuBar", "true");
            } else {
                for (LookAndFeelInfo ui : UIManager.getInstalledLookAndFeels()) {
                    Log.info("Available look and feel: " + ui.getName() + " " + ui.getClassName());
                    if (ui.getName().equals("Nimbus")) {
                        Log.info("Setting look and feel: {0}", ui.getClassName());
                        UIManager.setLookAndFeel(ui.getClassName());
                        break;
                    }
                    ;
                }
            }

            // If btcpd is currently not running, do a startup of the daemon as a child process
            // It may be started but not ready - then also show dialog
            BTCPInstallationObserver initialInstallationObserver =
                    new BTCPInstallationObserver(OSUtil.getProgramDirectory());
            DaemonInfo zcashdInfo = initialInstallationObserver.getDaemonInfo();
            initialInstallationObserver = null;

            BTCPClientCaller initialClientCaller = new BTCPClientCaller(OSUtil.getProgramDirectory());
            boolean daemonStartInProgress = false;
            try {
                if (zcashdInfo.status == DAEMON_STATUS.RUNNING) {
                    NetworkAndBlockchainInfo info = initialClientCaller.getNetworkAndBlockchainInfo();
                    // If more than 20 minutes behind in the blockchain - startup in progress
                    if ((System.currentTimeMillis() - info.lastBlockDate.getTime()) > (20 * 60 * 1000)) {
                        Log.info("Current blockchain synchronization date is " +
                                new Date(info.lastBlockDate.getTime()));
                        daemonStartInProgress = true;
                    }
                }
            } catch (WalletCallException wce) {
                if ((wce.getMessage().indexOf("{\"code\":-28") != -1) || // Started but not Ready
                        (wce.getMessage().indexOf("error code: -28") != -1)) {
                    Log.info("btcpd is currently starting...");
                    daemonStartInProgress = true;
                }
            }

            StartupProgressDialog startupBar = null;
            if ((zcashdInfo.status != DAEMON_STATUS.RUNNING) || (daemonStartInProgress)) {
                Log.info(
                        "btcpd is not running at the moment or has not started/synchronized 100% - showing splash...");
                startupBar = new StartupProgressDialog(initialClientCaller);
                startupBar.setVisible(true);
                startupBar.waitForStartup();
            }
            initialClientCaller = null;

            // Main GUI is created here
            BTCPWalletUI ui = new BTCPWalletUI(startupBar,title);
            ui.setVisible(true);

        } catch (InstallationDetectionException ide) {
            Log.error("Installation Error: ", ide);
            JOptionPane.showMessageDialog(
                    null,
                    "This program was started in directory: " + OSUtil.getProgramDirectory() + "\n" +
                            ide.getMessage() + "\n" +
                            "See the console/logfile output for more detailed error information!",
                    "Installation Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        } catch (WalletCallException wce) {
            Log.error("WalletCall Error: ", wce);

            if ((wce.getMessage().indexOf("{\"code\":-28,\"message\"") != -1) ||
                    (wce.getMessage().indexOf("error code: -28") != -1)) {
                JOptionPane.showMessageDialog(
                        null,
                        "It appears that btcpd has been started but is not ready to accept wallet\n" +
                                "connections. It is still loading the wallet and blockchain. Please try\n" +
                                "restarting this program.",
                        "Daemon Error",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(
                        null,
                        "There was a problem communicating with the Bitcoin Private daemon/wallet. \n" +
                                "Please ensure that the Bitcoin Private server btcpd is started (e.g. via \n" +
                                "command  \"btcpd --daemon\"). Error Message: \n" +
                                wce.getMessage() +
                                "See the console/logfile output for more detailed error information!",
                        "Daemon Error",
                        JOptionPane.ERROR_MESSAGE);
            }

            System.exit(2);
        } catch (Exception e) {
            Log.error("Unexpected error: ", e);
            JOptionPane.showMessageDialog(
                    null,
                    "An unexpected error (Exception) has occurred: \n" + e.getMessage() + "\n" +
                            "See the console/logfile output for more detailed error information!",
                    "Unexpected Exception",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(3);
        } catch (Error err) {
            // Last resort catch for unexpected problems - just to inform the user
            err.printStackTrace();
            JOptionPane.showMessageDialog(
                    null,
                    "An unexpected error has occurred: \n" + err.getMessage() + "\n" +
                            "See the console/logfile output for more detailed error information!",
                    "Unexpected Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(4);
        }
    }


    public static void possiblyCreateZENConfigFile()
            throws IOException {
        String blockchainDir = OSUtil.getBlockchainDirectory();
        File dir = new File(blockchainDir);

        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.error("ERROR: Could not create settings directory: " + dir.getCanonicalPath());
                throw new IOException("Could not create settings directory: " + dir.getCanonicalPath());
            }
        }

        File zenConfigFile = new File(dir, "btcprivate.conf");

        if (!zenConfigFile.exists()) {

            Log.info("btcprivate.conf (" + zenConfigFile.getCanonicalPath() +
                    ") does not exist. It will be created with default settings.");

            PrintStream configOut = new PrintStream(new FileOutputStream(zenConfigFile));
            Random r = new Random(System.currentTimeMillis());
            configOut.println("# Generated RPC credentials");
            configOut.println("rpcallowip=127.0.0.1");
            configOut.println("rpcuser=User" + Math.abs(r.nextInt()));
            configOut.println("rpcpassword=Pass" + Math.abs(r.nextInt()) + "" +
                    Math.abs(r.nextInt()) + "" +
                    Math.abs(r.nextInt()));

            for (String node : getDefaultConfig()) {
                configOut.println(node);
            }

            configOut.close();
        }

    }

    private static List<String> getDefaultConfig() {
        BufferedReader br = null;
        InputStream is = BTCPWalletUI.class.getResourceAsStream("/config/config.txt");
        br = new BufferedReader(new InputStreamReader(is));

        List<String> nodes = new ArrayList<>();
        String line;
        try {
            while ((line = br.readLine()) != null) {
                nodes.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return nodes;
    }
}
