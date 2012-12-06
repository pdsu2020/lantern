package org.lantern.state;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonView;
import org.lantern.LanternConstants;
import org.lantern.LanternHub;
import org.lantern.LanternUtils;
import org.lantern.Settings.CommandLineOption;
import org.lantern.Whitelist;
import org.lantern.state.Model.Persistent;
import org.lantern.state.Model.Run;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Base Lantern settings.
 */
public class Settings implements MutableSettings {

    private String userId = "";
    
    private String lang = "";
    
    private boolean autoStart = true;

    private boolean autoReport = true;
    
    private Mode mode = Mode.none;
    
    private int proxyPort = LanternConstants.LANTERN_LOCALHOST_HTTP_PORT;
    
    private boolean systemProxy = true;
    
    private boolean proxyAllSites = false;
    
    private boolean useGoogleOAuth2 = false;
    private String clientID;
    private String clientSecret;
    private String accessToken;
    private String refreshToken;
    
    private Set<String> inClosedBeta = new HashSet<String>();
    
    private Whitelist whitelist = new Whitelist();

    private boolean startAtLogin = true;

    private Set<String> proxies = new LinkedHashSet<String>();

    private boolean useTrustedPeers;

    private boolean useLaeProxies;

    private boolean useAnonymousPeers;

    private boolean useCentralProxies;

    private Set<String> stunServers = new HashSet<String>();

    private int serverPort = LanternUtils.randomPort();

    private boolean keychainEnabled;

    private boolean uiEnabled = true;

    private boolean bindToLocalhost;
    
    public enum Mode {
        give,
        get, 
        none
    }
    
    @JsonView({Run.class, Persistent.class})
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @JsonView(Run.class)
    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    @JsonView({Run.class, Persistent.class})
    public boolean isAutoStart() {
        return autoStart;
    }

    public void setAutoStart(final boolean autoStart) {
        this.autoStart = autoStart;
    }

    @JsonView({Run.class, Persistent.class})
    public boolean isAutoReport() {
        return autoReport;
    }

    public void setAutoReport(final boolean autoReport) {
        this.autoReport = autoReport;
    }

    @JsonView({Run.class, Persistent.class})
    public Mode getMode() {
        return mode;
    }

    public void setMode(final Mode mode) {
        this.mode = mode;
    }

    @JsonView({Run.class, Persistent.class})
    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(final int proxyPort) {
        this.proxyPort = proxyPort;
    }

    @JsonView({Run.class, Persistent.class})
    public boolean isSystemProxy() {
        return systemProxy;
    }

    @Override
    public void setSystemProxy(final boolean systemProxy) {
        this.systemProxy = systemProxy;
    }

    @JsonView({Run.class, Persistent.class})
    public boolean isProxyAllSites() {
        return proxyAllSites;
    }

    @Override
    public void setProxyAllSites(final boolean proxyAllSites) {
        this.proxyAllSites = proxyAllSites;
    }

    @JsonView({Run.class})
    public Collection<String> getProxiedSites() {
        return whitelist.getEntriesAsStrings();
    }

    public void setProxiedSites(final String[] proxiedSites) {
        whitelist.setStringEntries(proxiedSites);
    }

    @JsonView({Persistent.class})
    public Whitelist getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(Whitelist whitelist) {
        this.whitelist = whitelist;
    }
    
    public void setClientID(final String clientID) {
        this.clientID = clientID;
    }

    public void setUseGoogleOAuth2(boolean useGoogleOAuth2) {
        this.useGoogleOAuth2 = useGoogleOAuth2;
    }

    @CommandLineOption
    @JsonView({Persistent.class})
    public boolean isUseGoogleOAuth2() {
        return useGoogleOAuth2;
    }

    @CommandLineOption
    @JsonView({Persistent.class})
    public String getClientID() {
        return clientID;
    }

    public void setClientSecret(final String clientSecret) {
        this.clientSecret = clientSecret;
    }

    @CommandLineOption
    @JsonView({Persistent.class})
    public String getClientSecret() {
        return clientSecret;
    }

    public void setAccessToken(final String accessToken) {
        this.accessToken = accessToken;
    }

    @CommandLineOption
    @JsonView({Persistent.class})
    public String getAccessToken() {
        return accessToken;
    }

    public void setRefreshToken(final String password) {
        this.refreshToken = password;
    }

    @CommandLineOption
    @JsonView({Persistent.class})
    public String getRefreshToken() {
        return refreshToken;
    }
    

    @JsonView({Persistent.class})
    public Set<String> getInClosedBeta() {
        return Sets.newHashSet(this.inClosedBeta);
    }

    public void setInClosedBeta(final Set<String> inClosedBeta) {
        this.inClosedBeta = ImmutableSet.copyOf(inClosedBeta);
    }

    @Override
    public void setGetMode(final boolean getMode) {
        if (getMode) {
            setMode(Mode.get);
        } else {
            setMode(Mode.give);
        }
    }

    @JsonView({Run.class, Persistent.class})
    public boolean isGetMode() {
        synchronized (mode) {
            if (mode == Mode.none) {
                if (LanternHub.censored().isCensored()) {
                    mode = Mode.get;
                } else {
                    mode = Mode.give;
                }
            }
            return mode == Mode.get;
        }
    }

    @JsonView({Run.class, Persistent.class})
    public boolean isStartAtLogin() {
        return this.startAtLogin;
    }
    
    @Override
    public void setStartAtLogin(final boolean startAtLogin) {
        this.startAtLogin = startAtLogin;
    }
    
    public void setProxies(final Set<String> proxies) {
        synchronized (this.proxies) {
            this.proxies = proxies;
        }
    }

    @JsonView({Persistent.class})
    public Set<String> getProxies() {
        synchronized (this.proxies) {
            return ImmutableSet.copyOf(this.proxies);
        }
    }
    
    public void addProxy(final String proxy) {
        // Don't store peer proxies on disk.
        if (!proxy.contains("@")) {
            this.proxies.add(proxy);
        }
    }

    public void removeProxy(final String proxy) {
        this.proxies.remove(proxy);
    }
    

    public void setUseTrustedPeers(final boolean useTrustedPeers) {
        this.useTrustedPeers = useTrustedPeers;
    }
    
    @JsonIgnore
    public boolean isUseTrustedPeers() {
        return useTrustedPeers;
    }

    public void setUseLaeProxies(boolean useLaeProxies) {
        this.useLaeProxies = useLaeProxies;
    }

    @JsonIgnore
    public boolean isUseLaeProxies() {
        return useLaeProxies;
    }

    public void setUseAnonymousPeers(boolean useAnonymousPeers) {
        this.useAnonymousPeers = useAnonymousPeers;
    }

    @JsonIgnore
    public boolean isUseAnonymousPeers() {
        return useAnonymousPeers;
    }

    public void setUseCentralProxies(final boolean useCentralProxies) {
        this.useCentralProxies = useCentralProxies;
    }

    @JsonIgnore
    public boolean isUseCentralProxies() {
        return useCentralProxies;
    }
    
    public void setStunServers(final Set<String> stunServers){
        this.stunServers = stunServers;
    }

    @JsonView({Run.class, Persistent.class})
    public Collection<String> getStunServers() {
        return stunServers;
    }
    
    public void setServerPort(final int serverPort) {
        this.serverPort = serverPort;
    }

    @JsonView({Persistent.class})
    public int getServerPort() {
        return serverPort;
    }
    
    public void setKeychainEnabled(boolean keychainEnabled) {
        this.keychainEnabled = keychainEnabled;
    }
    
    @JsonIgnore
    public boolean isKeychainEnabled() {
        return keychainEnabled;
    }
    
    public void setUiEnabled(boolean uiEnabled) {
        this.uiEnabled = uiEnabled;
    }

    @JsonIgnore
    public boolean isUiEnabled() {
        return uiEnabled;
    }
    
    public void setBindToLocalhost(final boolean bindToLocalhost) {
        this.bindToLocalhost = bindToLocalhost;
    }

    @JsonIgnore
    public boolean isBindToLocalhost() {
        return bindToLocalhost;
    }
}
