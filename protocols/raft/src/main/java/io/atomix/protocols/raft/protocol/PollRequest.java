/*
 * Copyright 2015-present Open Networking Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.atomix.protocols.raft.protocol;

import io.atomix.protocols.raft.cluster.MemberId;

import java.util.Objects;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Server poll request.
 * <p>
 * Poll requests aid in the implementation of the so-called "pre-vote" protocol. They are sent by followers
 * to all other servers prior to transitioning to the candidate state. This helps ensure that servers that
 * can't win elections do not disrupt existing leaders when e.g. rejoining the cluster after a partition.
 */
public class PollRequest extends AbstractRaftRequest {

  /**
   * Returns a new poll request builder.
   *
   * @return A new poll request builder.
   */
  public static Builder builder() {
    return new Builder();
  }

  private final long term;
  private final MemberId candidate;
  private final long logIndex;
  private final long logTerm;

  public PollRequest(long term, MemberId candidate, long logIndex, long logTerm) {
    this.term = term;
    this.candidate = candidate;
    this.logIndex = logIndex;
    this.logTerm = logTerm;
  }

  /**
   * Returns the requesting node's current term.
   *
   * @return The requesting node's current term.
   */
  public long term() {
    return term;
  }

  /**
   * Returns the candidate's address.
   *
   * @return The candidate's address.
   */
  public MemberId candidate() {
    return candidate;
  }

  /**
   * Returns the candidate's last log index.
   *
   * @return The candidate's last log index.
   */
  public long logIndex() {
    return logIndex;
  }

  /**
   * Returns the candidate's last log term.
   *
   * @return The candidate's last log term.
   */
  public long logTerm() {
    return logTerm;
  }

  @Override
  public int hashCode() {
    return Objects.hash(getClass(), term, candidate, logIndex, logTerm);
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof PollRequest) {
      PollRequest request = (PollRequest) object;
      return request.term == term
          && request.candidate == candidate
          && request.logIndex == logIndex
          && request.logTerm == logTerm;
    }
    return false;
  }

  @Override
  public String toString() {
    return toStringHelper(this)
        .add("term", term)
        .add("candidate", candidate)
        .add("logIndex", logIndex)
        .add("logTerm", logTerm)
        .toString();
  }

  /**
   * Poll request builder.
   */
  public static class Builder extends AbstractRaftRequest.Builder<Builder, PollRequest> {
    private long term = -1;
    private MemberId candidate;
    private long logIndex = -1;
    private long logTerm = -1;

    /**
     * Sets the request term.
     *
     * @param term The request term.
     * @return The poll request builder.
     * @throws IllegalArgumentException if {@code term} is negative
     */
    public Builder withTerm(long term) {
      checkArgument(term >= 0, "term must be positive");
      this.term = term;
      return this;
    }

    /**
     * Sets the request leader.
     *
     * @param candidate The request candidate.
     * @return The poll request builder.
     * @throws IllegalArgumentException if {@code candidate} is not positive
     */
    public Builder withCandidate(MemberId candidate) {
      this.candidate = checkNotNull(candidate, "candidate cannot be null");
      return this;
    }

    /**
     * Sets the request last log index.
     *
     * @param logIndex The request last log index.
     * @return The poll request builder.
     * @throws IllegalArgumentException if {@code index} is negative
     */
    public Builder withLogIndex(long logIndex) {
      checkArgument(logIndex >= 0, "logIndex must be positive");
      this.logIndex = logIndex;
      return this;
    }

    /**
     * Sets the request last log term.
     *
     * @param logTerm The request last log term.
     * @return The poll request builder.
     * @throws IllegalArgumentException if {@code term} is negative
     */
    public Builder withLogTerm(long logTerm) {
      checkArgument(logTerm >= 0, "logTerm must be positive");
      this.logTerm = logTerm;
      return this;
    }

    @Override
    protected void validate() {
      super.validate();
      checkArgument(term >= 0, "term must be positive");
      checkNotNull(candidate, "candidate cannot be null");
      checkArgument(logIndex >= 0, "logIndex must be positive");
      checkArgument(logTerm >= 0, "logTerm must be positive");
    }

    /**
     * @throws IllegalStateException if candidate is not positive or if term, logIndex or logTerm are negative
     */
    @Override
    public PollRequest build() {
      validate();
      return new PollRequest(term, candidate, logIndex, logTerm);
    }
  }
}
