FROM mysql:8

# Copy small script
COPY check.sh /check.sh
COPY schema.sql /schema.sql
RUN chmod +x /check.sh

ENTRYPOINT ["/check.sh"]
