# User Service

The user service handles account related tasks.

## Responsibilities

- Authentication
- User account management
- Driver account management
- Admin account management

## Necessary environment variables
This service uses the AWS SDK to send emails through SES, it requires access_key_id  and secret_access_key 

## Quirks

### User and Driver Deletion
The order table needs to be manually edited upon creation with jpa. 
The driver_id and user_id foreign keys must have ON DELETE SET NULL or deletion will result in an error.