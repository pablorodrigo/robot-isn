package isnbot.classes;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * LEGO Communication Protocol constants.
 * 
 */
public interface ConstantesLCP
{
	// Type de la commande (Octet 0)
	public static final byte	DIRECT_COMMAND_REPLY		= 0x00;
	public static final byte	SYSTEM_COMMAND_REPLY		= 0x01;
	public static final byte	REPLY_COMMAND				= 0x02;
	// On gagne environ 100ms
	public static final byte	DIRECT_COMMAND_NOREPLY		= (byte) 0x80;
	public static final byte	SYSTEM_COMMAND_NOREPLY		= (byte) 0x81;

	// Commandes système :
	public static final byte	OPEN_READ					= (byte) 0x80;
	public static final byte	OPEN_WRITE					= (byte) 0x81;
	public static final byte	READ						= (byte) 0x82;
	public static final byte	WRITE						= (byte) 0x83;
	public static final byte	CLOSE						= (byte) 0x84;
	public static final byte	DELETE						= (byte) 0x85;
	public static final byte	FIND_FIRST					= (byte) 0x86;
	public static final byte	FIND_NEXT					= (byte) 0x87;
	public static final byte	GET_FIRMWARE_VERSION		= (byte) 0x88;
	public static final byte	OPEN_WRITE_LINEAR			= (byte) 0x89;
	public static final byte	OPEN_READ_LINEAR			= (byte) 0x8A;
	public static final byte	OPEN_WRITE_DATA				= (byte) 0x8B;
	public static final byte	OPEN_APPEND_DATA			= (byte) 0x8C;
	public static final byte	BOOT						= (byte) 0x97;
	public static final byte	SET_BRICK_NAME				= (byte) 0x98;
	public static final byte	GET_DEVICE_INFO				= (byte) 0x9B;
	public static final byte	DELETE_USER_FLASH			= (byte) 0xA0;
	public static final byte	POLL_LENGTH					= (byte) 0xA1;
	public static final byte	POLL						= (byte) 0xA2;

	// Constantes Poll :
	public static final byte	POLL_BUFFER					= (byte) 0x00;
	public static final byte	HIGH_SPEED_BUFFER			= (byte) 0x01;

	// Commandes directes
	public static final byte	START_PROGRAM				= 0x00;
	public static final byte	STOP_PROGRAM				= 0x01;
	public static final byte	PLAY_SOUND_FILE				= 0x02;
	public static final byte	PLAY_TONE					= 0x03;
	public static final byte	SET_OUTPUT_STATE			= 0x04;
	public static final byte	SET_INPUT_MODE				= 0x05;
	public static final byte	GET_OUTPUT_STATE			= 0x06;
	public static final byte	GET_INPUT_VALUES			= 0x07;
	public static final byte	RESET_SCALED_INPUT_VALUE	= 0x08;
	public static final byte	MESSAGE_WRITE				= 0x09;
	public static final byte	RESET_MOTOR_POSITION		= 0x0A;
	public static final byte	GET_BATTERY_LEVEL			= 0x0B;
	public static final byte	STOP_SOUND_PLAYBACK			= 0x0C;
	public static final byte	KEEP_ALIVE					= 0x0D;
	public static final byte	LS_GET_STATUS				= 0x0E;
	public static final byte	LS_WRITE					= 0x0F;
	public static final byte	LS_READ						= 0x10;
	public static final byte	GET_CURRENT_PROGRAM_NAME	= 0x11;
	public static final byte	MESSAGE_READ				= 0x13;

	// Output state constants
	// "Mode":
	/** Turn on the specified motor */
	public static final byte	MOTORON						= 0x01;
	/** Use run/brake instead of run/float in PWM */
	public static final byte	BRAKE						= 0x02;
	/** Turns on the regulation */
	public static final byte	REGULATED					= 0x04;

	// "Regulation Mode":
	public static final byte	REGULATION_MODE_IDLE		= 0x00;
	public static final byte	REGULATION_MODE_MOTOR_SPEED	= 0x01;
	public static final byte	REGULATION_MODE_MOTOR_SYNC	= 0x02;

	// "RUN_STATE":
	public static final byte	MOTOR_RUN_STATE_IDLE		= 0x00;
	public static final byte	MOTOR_RUN_STATE_RAMPUP		= 0x10;
	public static final byte	MOTOR_RUN_STATE_RUNNING		= 0x20;
	public static final byte	MOTOR_RUN_STATE_RAMPDOWN	= 0x40;
	public static final int		MOTOR_LIMIT_NONE			= 0;
	public static final int		MOTOR_TURNRATIO_NONE		= 0;

	// "Type capteur":
	public static final byte	NO_SENSOR					= 0x00;
	public static final byte	SWITCH						= 0x01;
	public static final byte	TEMPERATURE					= 0x02;
	public static final byte	REFLECTION					= 0x03;
	public static final byte	ANGLE						= 0x04;
	public static final byte	LIGHT_ACTIVE				= 0x05;
	public static final byte	LIGHT_INACTIVE				= 0x06;
	public static final byte	SOUND_DB					= 0x07;
	public static final byte	SOUND_DBA					= 0x08;
	public static final byte	CUSTOM						= 0x09;
	public static final byte	LOWSPEED					= 0x0A;
	public static final byte	LOWSPEED_9V					= 0x0B;
	public static final byte	NO_OF_SENSOR_TYPES			= 0x0C;
	public static final byte	FLOODLIGHT_RED				= 0x0e;
	public static final byte	FLOODLIGHT_GREEN			= 0x0f;
	public static final byte	FLOODLIGHT_BLUE				= 0x10;
	public static final byte	FLOODLIGHT_OFF				= 0x11;
	public static final byte	ROTATION					= 0x12;

	// "Mode capteur":
	public static final byte	RAWMODE						= 0x00;
	public static final byte	BOOLEANMODE					= 0x20;
	public static final byte	TRANSITIONCNTMODE			= 0x40;
	public static final byte	PERIODCOUNTERMODE			= 0x60;
	public static final byte	PCTFULLSCALEMODE			= (byte) 0x80;
	public static final byte	CELSIUSMODE					= (byte) 0xA0;
	public static final byte	FAHRENHEITMODE				= (byte) 0xC0;
	public static final byte	ANGLESTEPSMODE				= (byte) 0xE0;
	public static final byte	SLOPEMASK					= 0x1F;
	public static final byte	MODEMASK					= (byte) 0xE0;
	public static final byte	CONTINUOUSMODE				= 0x02;

	// "Numero Port":
	public static final int		PORT_1						= 0;
	public static final int		PORT_2						= 1;
	public static final int		PORT_3						= 2;
	public static final int		PORT_4						= 3;

	// "Nom Port":
	public static final int		PORT_A						= 0;
	public static final int		PORT_B						= 1;
	public static final int		PORT_C						= 2;

	public static final int		PORT_A_ROTATION				= 7;
	public static final int		PORT_B_ROTATION				= 8;
	public static final int		PORT_C_ROTATION				= 9;

	public static final int		PORT_DEFAUT					= 9;

	// "ID Capteur"
	public static final int		ID_TACTILE					= 0;
	public static final int		ID_PHOTOSENSIBLE			= 1;
	public static final int		ID_ULTRASONS				= 2;
	public static final int		ID_AUCUN					= 3;
	public static final int		ID_CAPTEUR_ROTATION			= 8;

	// "ID Moteur"
	public static final int		ID_CONNECTE					= 0;
	public static final int		ID_DECONNECTE				= 1;

	// "Sensor Name"
	public static final String	CAPTEUR_TACTILE				= "Capteur tactile";
	public static final String	CAPTEUR_PHOTOSENSIBLE		= "Capteur photosensible";
	public static final String	CAPTEUR_ULTRASONS			= "Capteur à ultrasons";
	public static final String	CAPTEUR_ROTATION			= "Capteur de rotation";
	public static final String	CAPTEUR_AUCUN				= "Aucun capteur";
}
