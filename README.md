# Syncro
# ------------------------------------------------------------
# |  Syncro - um aplicativo para sincronizacao de Legendas   |
# |      (c) 2007, 2025 Gustavo Santos (gugahh.br@gmail.com) |
# ------------------------------------------------------------
# - Utilize-o para adiantar ou atrasar as legendas de um arquivo de legendas *.srt.
# - Desenvolvido em Java 8. O desenvolvimento (em Java 5) comecou em 2007!
# - Este software e Freeware! 
#
# Utilizacao:
# -----------
# java -jar syncro.jar [-help] -- Exibe o help completo.
#
# * Modo [-adjust]  Utilizado para ajustar tempos de legendas.
# permite que todas (ou algumas) legendas do arquivo sejam adiantadas ou atrasadas.
#             Parametros arquivo e tempo sao obrigatorios.
#  Exemplos:
#  --------
#  * Para atrasar todas as legendas de heroes.srt em 1 minuto e 13 segundos:
#          java -jar syncro.jar -adjust heroes.srt 1:13s
#
#  * Para adiantar as legendas de heroes2.srt em 5 segundos 
#    e 300 milesimos, a partir da 2a. legenda:
#          java -jar syncro.jar -adjust heroes2.srt -5.3s 2 
#
#  ------------------------------
# * Modo [-renum]   Utilizado para renumerar as legendas.
#
#       java -jar syncro.jar [-renum [arquivo] [indiceorig] [indicenovo] ] [-nobak]
#
#  Exemplo:
#  --------
#       java -jar syncro.jar -renum heroes.srt 1 3 [-nobak]\n
#           o comando acima irá renumerar todas as legendas a partir da
#           legenda 1. Esta se tornara a legenda 3,
#           a legenda 2 se tornara a 4, e assim por diante
#
# Ha ainda outros modos, que documentarei posteriormente.


