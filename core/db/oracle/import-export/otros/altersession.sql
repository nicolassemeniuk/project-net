-- Cambio de los parametros de sesion antes de exportar la base de datos
ALTER SESSION set NLS_LANGUAGE = 'AMERICAN';
ALTER SESSION set NLS_TERRITORY = 'AMERICA';
ALTER SESSION set NLS_ISO_CURRENCY = 'AMERICA';
--ALTER SESSION set NLS_CURRENCY = '$'; --Opcional?

-- SELECT * from NLS_SESSION_PARAMETERS;
