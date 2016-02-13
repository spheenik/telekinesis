// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: encrypted_app_ticket.proto

package telekinesis.message.proto.generated;

public final class AppTicket {
  private AppTicket() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public interface EncryptedAppTicketOrBuilder extends
      // @@protoc_insertion_point(interface_extends:EncryptedAppTicket)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>optional uint32 ticket_version_no = 1;</code>
     */
    boolean hasTicketVersionNo();
    /**
     * <code>optional uint32 ticket_version_no = 1;</code>
     */
    int getTicketVersionNo();

    /**
     * <code>optional uint32 crc_encryptedticket = 2;</code>
     */
    boolean hasCrcEncryptedticket();
    /**
     * <code>optional uint32 crc_encryptedticket = 2;</code>
     */
    int getCrcEncryptedticket();

    /**
     * <code>optional uint32 cb_encrypteduserdata = 3;</code>
     */
    boolean hasCbEncrypteduserdata();
    /**
     * <code>optional uint32 cb_encrypteduserdata = 3;</code>
     */
    int getCbEncrypteduserdata();

    /**
     * <code>optional uint32 cb_encrypted_appownershipticket = 4;</code>
     */
    boolean hasCbEncryptedAppownershipticket();
    /**
     * <code>optional uint32 cb_encrypted_appownershipticket = 4;</code>
     */
    int getCbEncryptedAppownershipticket();

    /**
     * <code>optional bytes encrypted_ticket = 5;</code>
     */
    boolean hasEncryptedTicket();
    /**
     * <code>optional bytes encrypted_ticket = 5;</code>
     */
    com.google.protobuf.ByteString getEncryptedTicket();
  }
  /**
   * Protobuf type {@code EncryptedAppTicket}
   */
  public static final class EncryptedAppTicket extends
      com.google.protobuf.GeneratedMessage implements
      // @@protoc_insertion_point(message_implements:EncryptedAppTicket)
      EncryptedAppTicketOrBuilder {
    // Use EncryptedAppTicket.newBuilder() to construct.
    private EncryptedAppTicket(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
      super(builder);
      this.unknownFields = builder.getUnknownFields();
    }
    private EncryptedAppTicket(boolean noInit) { this.unknownFields = com.google.protobuf.UnknownFieldSet.getDefaultInstance(); }

    private static final EncryptedAppTicket defaultInstance;
    public static EncryptedAppTicket getDefaultInstance() {
      return defaultInstance;
    }

    public EncryptedAppTicket getDefaultInstanceForType() {
      return defaultInstance;
    }

    private final com.google.protobuf.UnknownFieldSet unknownFields;
    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
      return this.unknownFields;
    }
    private EncryptedAppTicket(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      initFields();
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
            case 8: {
              bitField0_ |= 0x00000001;
              ticketVersionNo_ = input.readUInt32();
              break;
            }
            case 16: {
              bitField0_ |= 0x00000002;
              crcEncryptedticket_ = input.readUInt32();
              break;
            }
            case 24: {
              bitField0_ |= 0x00000004;
              cbEncrypteduserdata_ = input.readUInt32();
              break;
            }
            case 32: {
              bitField0_ |= 0x00000008;
              cbEncryptedAppownershipticket_ = input.readUInt32();
              break;
            }
            case 42: {
              bitField0_ |= 0x00000010;
              encryptedTicket_ = input.readBytes();
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e.getMessage()).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return telekinesis.message.proto.generated.AppTicket.internal_static_EncryptedAppTicket_descriptor;
    }

    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return telekinesis.message.proto.generated.AppTicket.internal_static_EncryptedAppTicket_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              telekinesis.message.proto.generated.AppTicket.EncryptedAppTicket.class, telekinesis.message.proto.generated.AppTicket.EncryptedAppTicket.Builder.class);
    }

    public static com.google.protobuf.Parser<EncryptedAppTicket> PARSER =
        new com.google.protobuf.AbstractParser<EncryptedAppTicket>() {
      public EncryptedAppTicket parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new EncryptedAppTicket(input, extensionRegistry);
      }
    };

    @java.lang.Override
    public com.google.protobuf.Parser<EncryptedAppTicket> getParserForType() {
      return PARSER;
    }

    private int bitField0_;
    public static final int TICKET_VERSION_NO_FIELD_NUMBER = 1;
    private int ticketVersionNo_;
    /**
     * <code>optional uint32 ticket_version_no = 1;</code>
     */
    public boolean hasTicketVersionNo() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    /**
     * <code>optional uint32 ticket_version_no = 1;</code>
     */
    public int getTicketVersionNo() {
      return ticketVersionNo_;
    }

    public static final int CRC_ENCRYPTEDTICKET_FIELD_NUMBER = 2;
    private int crcEncryptedticket_;
    /**
     * <code>optional uint32 crc_encryptedticket = 2;</code>
     */
    public boolean hasCrcEncryptedticket() {
      return ((bitField0_ & 0x00000002) == 0x00000002);
    }
    /**
     * <code>optional uint32 crc_encryptedticket = 2;</code>
     */
    public int getCrcEncryptedticket() {
      return crcEncryptedticket_;
    }

    public static final int CB_ENCRYPTEDUSERDATA_FIELD_NUMBER = 3;
    private int cbEncrypteduserdata_;
    /**
     * <code>optional uint32 cb_encrypteduserdata = 3;</code>
     */
    public boolean hasCbEncrypteduserdata() {
      return ((bitField0_ & 0x00000004) == 0x00000004);
    }
    /**
     * <code>optional uint32 cb_encrypteduserdata = 3;</code>
     */
    public int getCbEncrypteduserdata() {
      return cbEncrypteduserdata_;
    }

    public static final int CB_ENCRYPTED_APPOWNERSHIPTICKET_FIELD_NUMBER = 4;
    private int cbEncryptedAppownershipticket_;
    /**
     * <code>optional uint32 cb_encrypted_appownershipticket = 4;</code>
     */
    public boolean hasCbEncryptedAppownershipticket() {
      return ((bitField0_ & 0x00000008) == 0x00000008);
    }
    /**
     * <code>optional uint32 cb_encrypted_appownershipticket = 4;</code>
     */
    public int getCbEncryptedAppownershipticket() {
      return cbEncryptedAppownershipticket_;
    }

    public static final int ENCRYPTED_TICKET_FIELD_NUMBER = 5;
    private com.google.protobuf.ByteString encryptedTicket_;
    /**
     * <code>optional bytes encrypted_ticket = 5;</code>
     */
    public boolean hasEncryptedTicket() {
      return ((bitField0_ & 0x00000010) == 0x00000010);
    }
    /**
     * <code>optional bytes encrypted_ticket = 5;</code>
     */
    public com.google.protobuf.ByteString getEncryptedTicket() {
      return encryptedTicket_;
    }

    private void initFields() {
      ticketVersionNo_ = 0;
      crcEncryptedticket_ = 0;
      cbEncrypteduserdata_ = 0;
      cbEncryptedAppownershipticket_ = 0;
      encryptedTicket_ = com.google.protobuf.ByteString.EMPTY;
    }
    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        output.writeUInt32(1, ticketVersionNo_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        output.writeUInt32(2, crcEncryptedticket_);
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        output.writeUInt32(3, cbEncrypteduserdata_);
      }
      if (((bitField0_ & 0x00000008) == 0x00000008)) {
        output.writeUInt32(4, cbEncryptedAppownershipticket_);
      }
      if (((bitField0_ & 0x00000010) == 0x00000010)) {
        output.writeBytes(5, encryptedTicket_);
      }
      getUnknownFields().writeTo(output);
    }

    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;

      size = 0;
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt32Size(1, ticketVersionNo_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt32Size(2, crcEncryptedticket_);
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt32Size(3, cbEncrypteduserdata_);
      }
      if (((bitField0_ & 0x00000008) == 0x00000008)) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt32Size(4, cbEncryptedAppownershipticket_);
      }
      if (((bitField0_ & 0x00000010) == 0x00000010)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(5, encryptedTicket_);
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }

    private static final long serialVersionUID = 0L;
    @java.lang.Override
    protected java.lang.Object writeReplace()
        throws java.io.ObjectStreamException {
      return super.writeReplace();
    }

    public static telekinesis.message.proto.generated.AppTicket.EncryptedAppTicket parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static telekinesis.message.proto.generated.AppTicket.EncryptedAppTicket parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static telekinesis.message.proto.generated.AppTicket.EncryptedAppTicket parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static telekinesis.message.proto.generated.AppTicket.EncryptedAppTicket parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static telekinesis.message.proto.generated.AppTicket.EncryptedAppTicket parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static telekinesis.message.proto.generated.AppTicket.EncryptedAppTicket parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }
    public static telekinesis.message.proto.generated.AppTicket.EncryptedAppTicket parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input);
    }
    public static telekinesis.message.proto.generated.AppTicket.EncryptedAppTicket parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input, extensionRegistry);
    }
    public static telekinesis.message.proto.generated.AppTicket.EncryptedAppTicket parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static telekinesis.message.proto.generated.AppTicket.EncryptedAppTicket parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }

    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(telekinesis.message.proto.generated.AppTicket.EncryptedAppTicket prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessage.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code EncryptedAppTicket}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:EncryptedAppTicket)
        telekinesis.message.proto.generated.AppTicket.EncryptedAppTicketOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return telekinesis.message.proto.generated.AppTicket.internal_static_EncryptedAppTicket_descriptor;
      }

      protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return telekinesis.message.proto.generated.AppTicket.internal_static_EncryptedAppTicket_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                telekinesis.message.proto.generated.AppTicket.EncryptedAppTicket.class, telekinesis.message.proto.generated.AppTicket.EncryptedAppTicket.Builder.class);
      }

      // Construct using telekinesis.message.proto.generated.AppTicket.EncryptedAppTicket.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessage.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders) {
        }
      }
      private static Builder create() {
        return new Builder();
      }

      public Builder clear() {
        super.clear();
        ticketVersionNo_ = 0;
        bitField0_ = (bitField0_ & ~0x00000001);
        crcEncryptedticket_ = 0;
        bitField0_ = (bitField0_ & ~0x00000002);
        cbEncrypteduserdata_ = 0;
        bitField0_ = (bitField0_ & ~0x00000004);
        cbEncryptedAppownershipticket_ = 0;
        bitField0_ = (bitField0_ & ~0x00000008);
        encryptedTicket_ = com.google.protobuf.ByteString.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000010);
        return this;
      }

      public Builder clone() {
        return create().mergeFrom(buildPartial());
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return telekinesis.message.proto.generated.AppTicket.internal_static_EncryptedAppTicket_descriptor;
      }

      public telekinesis.message.proto.generated.AppTicket.EncryptedAppTicket getDefaultInstanceForType() {
        return telekinesis.message.proto.generated.AppTicket.EncryptedAppTicket.getDefaultInstance();
      }

      public telekinesis.message.proto.generated.AppTicket.EncryptedAppTicket build() {
        telekinesis.message.proto.generated.AppTicket.EncryptedAppTicket result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public telekinesis.message.proto.generated.AppTicket.EncryptedAppTicket buildPartial() {
        telekinesis.message.proto.generated.AppTicket.EncryptedAppTicket result = new telekinesis.message.proto.generated.AppTicket.EncryptedAppTicket(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        result.ticketVersionNo_ = ticketVersionNo_;
        if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
          to_bitField0_ |= 0x00000002;
        }
        result.crcEncryptedticket_ = crcEncryptedticket_;
        if (((from_bitField0_ & 0x00000004) == 0x00000004)) {
          to_bitField0_ |= 0x00000004;
        }
        result.cbEncrypteduserdata_ = cbEncrypteduserdata_;
        if (((from_bitField0_ & 0x00000008) == 0x00000008)) {
          to_bitField0_ |= 0x00000008;
        }
        result.cbEncryptedAppownershipticket_ = cbEncryptedAppownershipticket_;
        if (((from_bitField0_ & 0x00000010) == 0x00000010)) {
          to_bitField0_ |= 0x00000010;
        }
        result.encryptedTicket_ = encryptedTicket_;
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof telekinesis.message.proto.generated.AppTicket.EncryptedAppTicket) {
          return mergeFrom((telekinesis.message.proto.generated.AppTicket.EncryptedAppTicket)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(telekinesis.message.proto.generated.AppTicket.EncryptedAppTicket other) {
        if (other == telekinesis.message.proto.generated.AppTicket.EncryptedAppTicket.getDefaultInstance()) return this;
        if (other.hasTicketVersionNo()) {
          setTicketVersionNo(other.getTicketVersionNo());
        }
        if (other.hasCrcEncryptedticket()) {
          setCrcEncryptedticket(other.getCrcEncryptedticket());
        }
        if (other.hasCbEncrypteduserdata()) {
          setCbEncrypteduserdata(other.getCbEncrypteduserdata());
        }
        if (other.hasCbEncryptedAppownershipticket()) {
          setCbEncryptedAppownershipticket(other.getCbEncryptedAppownershipticket());
        }
        if (other.hasEncryptedTicket()) {
          setEncryptedTicket(other.getEncryptedTicket());
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }

      public final boolean isInitialized() {
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        telekinesis.message.proto.generated.AppTicket.EncryptedAppTicket parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (telekinesis.message.proto.generated.AppTicket.EncryptedAppTicket) e.getUnfinishedMessage();
          throw e;
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private int ticketVersionNo_ ;
      /**
       * <code>optional uint32 ticket_version_no = 1;</code>
       */
      public boolean hasTicketVersionNo() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      /**
       * <code>optional uint32 ticket_version_no = 1;</code>
       */
      public int getTicketVersionNo() {
        return ticketVersionNo_;
      }
      /**
       * <code>optional uint32 ticket_version_no = 1;</code>
       */
      public Builder setTicketVersionNo(int value) {
        bitField0_ |= 0x00000001;
        ticketVersionNo_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional uint32 ticket_version_no = 1;</code>
       */
      public Builder clearTicketVersionNo() {
        bitField0_ = (bitField0_ & ~0x00000001);
        ticketVersionNo_ = 0;
        onChanged();
        return this;
      }

      private int crcEncryptedticket_ ;
      /**
       * <code>optional uint32 crc_encryptedticket = 2;</code>
       */
      public boolean hasCrcEncryptedticket() {
        return ((bitField0_ & 0x00000002) == 0x00000002);
      }
      /**
       * <code>optional uint32 crc_encryptedticket = 2;</code>
       */
      public int getCrcEncryptedticket() {
        return crcEncryptedticket_;
      }
      /**
       * <code>optional uint32 crc_encryptedticket = 2;</code>
       */
      public Builder setCrcEncryptedticket(int value) {
        bitField0_ |= 0x00000002;
        crcEncryptedticket_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional uint32 crc_encryptedticket = 2;</code>
       */
      public Builder clearCrcEncryptedticket() {
        bitField0_ = (bitField0_ & ~0x00000002);
        crcEncryptedticket_ = 0;
        onChanged();
        return this;
      }

      private int cbEncrypteduserdata_ ;
      /**
       * <code>optional uint32 cb_encrypteduserdata = 3;</code>
       */
      public boolean hasCbEncrypteduserdata() {
        return ((bitField0_ & 0x00000004) == 0x00000004);
      }
      /**
       * <code>optional uint32 cb_encrypteduserdata = 3;</code>
       */
      public int getCbEncrypteduserdata() {
        return cbEncrypteduserdata_;
      }
      /**
       * <code>optional uint32 cb_encrypteduserdata = 3;</code>
       */
      public Builder setCbEncrypteduserdata(int value) {
        bitField0_ |= 0x00000004;
        cbEncrypteduserdata_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional uint32 cb_encrypteduserdata = 3;</code>
       */
      public Builder clearCbEncrypteduserdata() {
        bitField0_ = (bitField0_ & ~0x00000004);
        cbEncrypteduserdata_ = 0;
        onChanged();
        return this;
      }

      private int cbEncryptedAppownershipticket_ ;
      /**
       * <code>optional uint32 cb_encrypted_appownershipticket = 4;</code>
       */
      public boolean hasCbEncryptedAppownershipticket() {
        return ((bitField0_ & 0x00000008) == 0x00000008);
      }
      /**
       * <code>optional uint32 cb_encrypted_appownershipticket = 4;</code>
       */
      public int getCbEncryptedAppownershipticket() {
        return cbEncryptedAppownershipticket_;
      }
      /**
       * <code>optional uint32 cb_encrypted_appownershipticket = 4;</code>
       */
      public Builder setCbEncryptedAppownershipticket(int value) {
        bitField0_ |= 0x00000008;
        cbEncryptedAppownershipticket_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional uint32 cb_encrypted_appownershipticket = 4;</code>
       */
      public Builder clearCbEncryptedAppownershipticket() {
        bitField0_ = (bitField0_ & ~0x00000008);
        cbEncryptedAppownershipticket_ = 0;
        onChanged();
        return this;
      }

      private com.google.protobuf.ByteString encryptedTicket_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <code>optional bytes encrypted_ticket = 5;</code>
       */
      public boolean hasEncryptedTicket() {
        return ((bitField0_ & 0x00000010) == 0x00000010);
      }
      /**
       * <code>optional bytes encrypted_ticket = 5;</code>
       */
      public com.google.protobuf.ByteString getEncryptedTicket() {
        return encryptedTicket_;
      }
      /**
       * <code>optional bytes encrypted_ticket = 5;</code>
       */
      public Builder setEncryptedTicket(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000010;
        encryptedTicket_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional bytes encrypted_ticket = 5;</code>
       */
      public Builder clearEncryptedTicket() {
        bitField0_ = (bitField0_ & ~0x00000010);
        encryptedTicket_ = getDefaultInstance().getEncryptedTicket();
        onChanged();
        return this;
      }

      // @@protoc_insertion_point(builder_scope:EncryptedAppTicket)
    }

    static {
      defaultInstance = new EncryptedAppTicket(true);
      defaultInstance.initFields();
    }

    // @@protoc_insertion_point(class_scope:EncryptedAppTicket)
  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_EncryptedAppTicket_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_EncryptedAppTicket_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\032encrypted_app_ticket.proto\"\255\001\n\022Encrypt" +
      "edAppTicket\022\031\n\021ticket_version_no\030\001 \001(\r\022\033" +
      "\n\023crc_encryptedticket\030\002 \001(\r\022\034\n\024cb_encryp" +
      "teduserdata\030\003 \001(\r\022\'\n\037cb_encrypted_appown" +
      "ershipticket\030\004 \001(\r\022\030\n\020encrypted_ticket\030\005" +
      " \001(\014B5\n#telekinesis.message.proto.genera" +
      "tedB\tAppTicketH\001\200\001\000"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
    internal_static_EncryptedAppTicket_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_EncryptedAppTicket_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_EncryptedAppTicket_descriptor,
        new java.lang.String[] { "TicketVersionNo", "CrcEncryptedticket", "CbEncrypteduserdata", "CbEncryptedAppownershipticket", "EncryptedTicket", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
