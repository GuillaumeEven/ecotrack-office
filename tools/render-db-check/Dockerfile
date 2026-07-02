FROM mysql:8

# Copy small script
COPY check.sh /check.sh
RUN chmod +x /check.sh

ENTRYPOINT ["/check.sh"]
