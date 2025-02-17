package fr.ensim.interop.introrest.controller;

import fr.ensim.interop.introrest.model.telegram.utilitaire.ApiResponseUpdateTelegram;
import fr.ensim.interop.introrest.model.telegram.utilitaire.MeteoMsg;
import fr.ensim.interop.introrest.model.telegram.model.Joke;
import fr.ensim.interop.introrest.model.telegram.model.OpenWeather;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class MessageRestController {
	public ResponseEntity<ApiResponseUpdateTelegram> responseEntity;
	public String message_recu;
	public String weatherDescription;
	public Double temp;
	public static String messageMeteo;
	public String ville;
	public Long CHATID = 5405543053L;

	@Value("${telegram.api.url}")
	private String telegramApiUrl;

	/*@Value("${telegram.bot.id}")
	private String CHATID;*/



	public void listen(){
		RestTemplate restTemplate = new RestTemplate();
		responseEntity = restTemplate.getForEntity("https://api.telegram.org/bot5582335341:AAFVu90WyAdTIQUixHvtzgJzeyejBXT3n8w/getUpdates?offset=-1", ApiResponseUpdateTelegram .class);
		message_recu=responseEntity.getBody().getResult().get(0).getMessage().getText();
		System.out.println(responseEntity.getBody().getResult().get(0).getMessage().getText());

	}
public String getMeteo(){
	listen();
	if(message_recu.equalsIgnoreCase("Meteo")){

		OpenWeatherRestController.meteoVille("Le mans");
		OpenWeather res = OpenWeatherRestController.meteoVille("Le mans").getBody();
		weatherDescription = res.getWeather().get(0).getMain();
		temp = res.getMain().getTemp();
		System.out.println(weatherDescription+' '+temp);
		messageMeteo = "Weather in Le Mans"+'\n'+weatherDescription+'\n'+temp+ "°C";
		sendMeteoData();
		System.out.println("ffffffffff");
		return messageMeteo;
	}

	if(message_recu.toLowerCase().startsWith("meteo")){
		ville = message_recu.substring(6);
		OpenWeatherRestController.meteoVille(ville);
		OpenWeather res = OpenWeatherRestController.meteoVille(ville).getBody();
		weatherDescription = res.getWeather().get(0).getMain();
		temp = res.getMain().getTemp();
		System.out.println(weatherDescription+' '+temp);
		messageMeteo = "Weather in "+ville+'\n'+weatherDescription+'\n'+temp+ "°C";
		sendMeteoData();
		//System.out.println(ville);
		return messageMeteo;
	}

	else if(message_recu.equalsIgnoreCase("Joke")){
		Joke blague = JokeController.getRandomJoke().getBody();
		messageMeteo = blague.getTitre()+'\n'+blague.getTexte()+"\n Rate: "+blague.getRate()+"étoiles.";
		sendMessage(messageMeteo);
	}
	return "Aucun raitement associé a ce message";
}

public MeteoMsg sendMeteoData(){
	RestTemplate restTemplate = new RestTemplate();
	MeteoMsg meteoMsg = new MeteoMsg();
	meteoMsg.setText(messageMeteo);
	meteoMsg.setChat_id((CHATID));

	MeteoMsg msg = restTemplate.postForObject("https://api.telegram.org/bot5582335341:AAFVu90WyAdTIQUixHvtzgJzeyejBXT3n8w/sendMessage",
			meteoMsg,MeteoMsg.class
			);
	System.out.println(msg);
	return msg;

}


	public MeteoMsg sendMessage(String msg){
		RestTemplate restTemplate2 = new RestTemplate();
		MeteoMsg meteoMsg = new MeteoMsg();
		meteoMsg.setText(msg);
		meteoMsg.setChat_id((CHATID));

		MeteoMsg msg_ = restTemplate2.postForObject("https://api.telegram.org/bot5582335341:AAFVu90WyAdTIQUixHvtzgJzeyejBXT3n8w/sendMessage",
				meteoMsg,MeteoMsg.class
		);
		System.out.println(msg_);
		return msg_;

	}


	//Opérations sur la ressource Message
}
