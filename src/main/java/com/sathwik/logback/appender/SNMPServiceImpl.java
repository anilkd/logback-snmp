package com.sathwik.logback.appender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDUv1;
import org.snmp4j.Snmp;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author anil.dhulipalla
 */
public class SNMPServiceImpl implements SNMPService {
    private static final Logger log = LoggerFactory.getLogger(SNMPServiceImpl.class);
    private static final String PUBLIC_COMMUNITY = "public";

    public static final int RETRIES = 1;
    public static final int TIMEOUT = 1500;
    private SNMPConfig config;

    public SNMPServiceImpl(SNMPConfig config) {
        this.config = config;
    }

    @Override
    public void sendEvent(String message, String detail, String priority, String escalation) {
        Snmp snmp = null;

        try {

            log.debug("Starting sendTrap");

            //Use default UDP Transport Mapping unless informed otherwise
            //Default is as good a place as any to start
            snmp = new Snmp(new DefaultUdpTransportMapping());
            snmp.listen();

            log.debug("Create community target");

            //Create primary SNMP trap target
            CommunityTarget target = new CommunityTarget();
            target.setCommunity(new OctetString(config.getCommunity() != null ? config.getCommunity() : PUBLIC_COMMUNITY));
            target.setAddress(new UdpAddress(getOpenNmsHostIpAddress().getHostAddress() + "/" + config.getPort()));
            target.setRetries(RETRIES);
            target.setTimeout(TIMEOUT);
            target.setVersion(SnmpConstants.version1);

            String enterpriseOID = config.getEnterpriseId();
            OID oid = new OID(enterpriseOID);
            log.debug("Created OID Object: " + oid);

            log.debug("Create PDUv1");
            //Create the SNMP Trap message
            PDUv1 pdu = new PDUv1();
            //Sets the specific trap ID. If this value is set, setGenericTrap(int genericTrap) must be called with value ENTERPRISE_SPECIFIC.
            pdu.setGenericTrap(PDUv1.ENTERPRISE_SPECIFIC);
            pdu.setSpecificTrap(Integer.parseInt(config.getSpecificTrapType()));
            pdu.setEnterprise(oid);
            pdu.setAgentAddress(new IpAddress(getAgentAddress()));

            //Bind the Exception details to the OID
            pdu.add(new VariableBinding(new OID(enterpriseOID + ".1"), new OctetString(config.getSystemName())));
            pdu.add(new VariableBinding(new OID(enterpriseOID + ".2"), new OctetString(message == null ? new OctetString("") : new OctetString(message))));
            pdu.add(new VariableBinding(new OID(enterpriseOID + ".3"), new OctetString(priority)));
            // escalation can be null if a ResolveAlarm event is being sent
            pdu.add(new VariableBinding(new OID(enterpriseOID + ".4"), escalation == null ? new OctetString("") : new OctetString(escalation.toString())));
            if (detail != null) {
                pdu.add(new VariableBinding(new OID(enterpriseOID + ".5"), new OctetString(detail)));
            }

            log.debug("send pdu" + pdu);

            //send to SNMP traps
            snmp.send(pdu, target);

            log.debug("Finishing sendTrap");
        } catch (IOException ioe) {
            log.error("openNMS message " + message);
            log.error("openNMS priority " + priority);
            log.error("openNMS escalation " + escalation);
            log.error("Unable to send openNMS", ioe);
            throw new RuntimeException(ioe);
        } finally {
            //Close the snmp connection
            try {
                if (snmp != null) {
                    snmp.close();
                }
            } catch (IOException ioe) {
                log.error("Unable to close snmp connection", ioe);
            }
        }
    }

    private InetAddress getOpenNmsHostIpAddress() throws UnknownHostException {
        return InetAddress.getByName(config.getHost());
    }

    private InetAddress getUserIpAddress() throws UnknownHostException {
        return InetAddress.getByName(System.getProperty("user.name"));
    }

    private InetAddress getHostIpAddress() throws UnknownHostException {
        return InetAddress.getLocalHost();
    }

    public InetAddress getAgentAddress() throws UnknownHostException {
        InetAddress inetAddress;
        try {
            inetAddress = getUserIpAddress();
        } catch (UnknownHostException uhe) {
            inetAddress = getHostIpAddress();
        }
        return inetAddress;
    }
}
