cd $(dirname $0)
java -server -Xmx64m -classpath Java/triplea.jar games.strategy.engine.framework.HeadlessGameServer triplea.game.host.console=true triplea.game.host.ui=false triplea.game= triplea.server=true triplea.port=3304 triplea.lobby.host=173.255.229.134 triplea.lobby.port=3303 triplea.name=Bot1_YourServerName triplea.lobby.game.hostedBy=Bot1_YourServerName triplea.lobby.game.supportEmail=yourEmailName(AT)emailProvider.com triplea.lobby.game.comments="automated_hosting"
